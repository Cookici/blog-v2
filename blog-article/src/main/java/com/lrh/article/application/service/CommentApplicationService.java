package com.lrh.article.application.service;

import com.lrh.article.application.cqe.comment.*;
import com.lrh.article.application.dto.PageDTO;
import com.lrh.article.application.dto.comment.CommentDTO;
import com.lrh.article.application.dto.comment.CommentUserDTO;
import com.lrh.article.constants.CommentConstant;
import com.lrh.article.constants.RedisConstant;
import com.lrh.article.domain.entity.ArticleEntity;
import com.lrh.article.domain.entity.CommentEntity;
import com.lrh.article.domain.repository.CommentCacheRepository;
import com.lrh.article.domain.service.ArticleOperateService;
import com.lrh.article.domain.service.CommentOperateService;
import com.lrh.article.domain.vo.UserVO;
import com.lrh.article.infrastructure.client.UserClient;
import com.lrh.article.util.LockUtil;
import com.lrh.common.result.Result;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.application.service
 * @ClassName: CommentApplicationService
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/24 16:47
 */
@Service
public class CommentApplicationService {

    private final CommentOperateService commentOperateService;

    private final ArticleOperateService articleOperateService;

    private final UserClient userClient;

    private final CommentCacheRepository commentCacheRepository;

    private final RedissonClient redissonClient;

    public CommentApplicationService(CommentOperateService commentOperateService, UserClient userClient,
                                     ArticleOperateService articleOperateService, CommentCacheRepository commentCacheRepository, RedissonClient redissonClient) {
        this.commentOperateService = commentOperateService;
        this.userClient = userClient;
        this.articleOperateService = articleOperateService;
        this.commentCacheRepository = commentCacheRepository;
        this.redissonClient = redissonClient;
    }


    /**
     * 获取完整的评论列表
     *
     * @param commentEntityList 没有填充用户信息的评论列表
     * @return List<CommentDTO>
     */
    private List<CommentDTO> getFullCommentList(List<CommentEntity> commentEntityList) {
        if (commentEntityList == null || commentEntityList.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> uniqueUserIdList = Stream.concat(
                commentEntityList.stream().map(CommentEntity::getUserId),
                commentEntityList.stream().map(CommentEntity::getToUserId)
        ).distinct().collect(Collectors.toList());

        Result<Map<String, UserVO>> userList = userClient.getByIds(uniqueUserIdList);
        Map<String, UserVO> userIdForUser = userList.getData();

        List<CommentDTO> commentDTOList = new ArrayList<>();
        commentEntityList.forEach(commentEntity -> {
            UserVO userVO = userIdForUser.getOrDefault(commentEntity.getUserId(), new UserVO());
            UserVO toUserVO = userIdForUser.getOrDefault(commentEntity.getToUserId(), new UserVO());
            CommentDTO commentDTO = CommentDTO.fromEntity(commentEntity, userVO, toUserVO);
            commentDTOList.add(commentDTO);
        });
        return commentDTOList;
    }


    /**
     * 插入评论并清除相关缓存
     */
    public String insertComment(CommentInsertCommand command) {
        command.valid();

        // 构建锁键，使用统一的命名规则
        String lockKey;
        if (!CommentConstant.TOP_COMMENT_PARENT_ID.equals(command.getParentCommentId())) {
            // 子评论锁 - 以父评论ID为锁
            lockKey = String.format(RedisConstant.COMMENT_LOCK_PARENT, command.getParentCommentId());
        } else {
            // 顶级评论锁 - 以文章ID为锁
            lockKey = String.format(RedisConstant.COMMENT_LOCK_ARTICLE, command.getArticleId());
        }

        LockUtil lockUtil = new LockUtil(redissonClient);
        return lockUtil.tryLockAndReturn(lockKey, () -> {
            String commentId = commentOperateService.insertComment(command);
            
            // 清除相关缓存
            commentCacheRepository.deleteCommentCache(command.getArticleId());
            
            if (!CommentConstant.TOP_COMMENT_PARENT_ID.equals(command.getParentCommentId())) {
                commentCacheRepository.deleteCommentChildCache(command.getArticleId(), command.getParentCommentId());
                commentCacheRepository.deleteChildCommentCountCache(command.getArticleId(), command.getParentCommentId());
            }
            
            commentCacheRepository.deleteUserCommentCache(command.getUserId());
            
            return commentId;
        });
    }

    /**
     * 删除评论并清除相关缓存
     */
    public Boolean deleteComment(CommentDeleteCommand command) {
        command.valid();

        // 构建锁键，使用统一的命名规则
        String lockKey;
        if (!CommentConstant.TOP_COMMENT_PARENT_ID.equals(command.getParentCommentId())) {
            // 子评论锁 - 以父评论ID为锁
            lockKey = String.format(RedisConstant.COMMENT_LOCK_PARENT, command.getParentCommentId());
        } else {
            // 顶级评论锁 - 以评论ID为锁
            lockKey = String.format(RedisConstant.COMMENT_LOCK_COMMENT, command.getCommentId());
        }

        LockUtil lockUtil = new LockUtil(redissonClient);
        return lockUtil.tryLockAndReturn(lockKey, () -> {
            commentOperateService.deleteComment(command);
            
            // 清除相关缓存
            commentCacheRepository.deleteCommentCache(command.getArticleId());
            
            if (!CommentConstant.TOP_COMMENT_PARENT_ID.equals(command.getParentCommentId())) {
                commentCacheRepository.deleteCommentChildCache(command.getArticleId(), command.getParentCommentId());
                commentCacheRepository.deleteChildCommentCountCache(command.getArticleId(), command.getParentCommentId());
            } else {
                commentCacheRepository.deleteAllChildCommentsCache(command.getArticleId(), command.getCommentId());
            }
            
            commentCacheRepository.deleteUserCommentCache(command.getUserId());
            
            return true;
        });
    }

    public PageDTO<CommentDTO> pageComments(CommentPageQuery query) {
        query.valid();

        // 使用RedisConstant定义的常量构建锁键
        String countLockKey = String.format(RedisConstant.COMMENT_COUNT_LOCK, query.getArticleId());
        LockUtil lockUtil = new LockUtil(redissonClient);

        // 1. 尝试从缓存获取评论总数
        Long totalComments = commentCacheRepository.getCommentCount(query.getArticleId());
        if (totalComments == null) {
            // 缓存未命中，使用分布式锁防止缓存击穿
            // 双重检查，避免重复查询数据库
            // 缓存未命中，从数据库获取
            // 存入缓存 - 无论评论数是否为0都缓存
            totalComments = lockUtil.tryLockAndReturn(countLockKey, () -> {
                // 双重检查，避免重复查询数据库
                Long cachedTotal = commentCacheRepository.getCommentCount(query.getArticleId());
                if (cachedTotal != null) {
                    return cachedTotal;
                }

                // 缓存未命中，从数据库获取
                Long count = commentOperateService.countCommentsPage(query);
                // 存入缓存 - 无论评论数是否为0都缓存
                if (count != null) {
                    commentCacheRepository.saveCommentCount(query.getArticleId(), count);
                    return count;
                } else {
                    commentCacheRepository.saveCommentCount(query.getArticleId(), 0L);
                    return 0L;
                }
            });
        }

        if (totalComments == 0L) {
            return new PageDTO<>();
        }

        // 尝试从缓存获取评论列表
        List<CommentDTO> cachedComments = commentCacheRepository.getTopComments(
                query.getArticleId(), query.getPage(), query.getPageSize());

        if (cachedComments != null) {
            // 缓存命中，直接返回
            return PageDTO.<CommentDTO>builder()
                    .total(totalComments)
                    .data(cachedComments)
                    .page(query.getPage())
                    .pageSize(query.getPageSize())
                    .build();
        }

        // 缓存未命中，使用分布式锁防止缓存击穿
        String listLockKey = String.format(RedisConstant.COMMENT_LIST_LOCK, 
                query.getArticleId(), query.getPage(), query.getPageSize());
        
        // 确保totalComments是final或有效final
        final Long finalTotalComments = totalComments;
        return lockUtil.tryLockAndReturn(listLockKey, () -> {
            // 双重检查，避免重复查询数据库
            List<CommentDTO> doubleCheckCachedComments = commentCacheRepository.getTopComments(
                    query.getArticleId(), query.getPage(), query.getPageSize());
            
            if (doubleCheckCachedComments != null) {
                return PageDTO.<CommentDTO>builder()
                        .total(finalTotalComments)
                        .data(doubleCheckCachedComments)
                        .page(query.getPage())
                        .pageSize(query.getPageSize())
                        .build();
            }
            
            // 缓存未命中，从数据库获取
            List<CommentEntity> commentEntityList = commentOperateService.getTopCommentsPage(query);
            List<CommentDTO> commentDTOList = getFullCommentList(commentEntityList);

            List<CommentDTO> results = commentDTOList.stream()
                    .filter(commentDTO -> Objects.equals(commentDTO.getParentCommentId(), "0"))
                    .collect(Collectors.toList());

            // 存入缓存 - 即使为空列表也缓存，避免缓存穿透
            commentCacheRepository.saveTopComments(query.getArticleId(), query.getPage(), query.getPageSize(), results);

            return PageDTO.<CommentDTO>builder()
                    .total(finalTotalComments)
                    .data(results)
                    .page(query.getPage())
                    .pageSize(query.getPageSize())
                    .build();
        });
    }

    public PageDTO<CommentDTO> pageChildComments(CommentChildPageQuery query) {
        query.valid();

        String countCacheKey = String.format(RedisConstant.COMMENT_CHILD_COUNT,
            query.getArticleId(), query.getCommentId());
            
        // 使用RedisConstant定义的常量构建锁键
        String countLockKey = String.format(RedisConstant.COMMENT_CHILD_COUNT_LOCK, 
                query.getArticleId(), query.getCommentId());
        LockUtil lockUtil = new LockUtil(redissonClient);

        // 尝试从缓存获取子评论总数
        Long totalChildComments = commentCacheRepository.getCommentCount(countCacheKey);
        if (totalChildComments == null) {
            // 缓存未命中，使用分布式锁防止缓存击穿
            // 双重检查，避免重复查询数据库
            // 缓存未命中，从数据库获取
            // 存入缓存 - 无论评论数是否为0都缓存
            totalChildComments = lockUtil.tryLockAndReturn(countLockKey, () -> {
                // 双重检查，避免重复查询数据库
                Long cachedTotal = commentCacheRepository.getCommentCount(countCacheKey);
                if (cachedTotal != null) {
                    return cachedTotal;
                }

                // 缓存未命中，从数据库获取
                Long count = commentOperateService.countChildCommentsPage(query);
                // 存入缓存 - 无论评论数是否为0都缓存
                if (count != null) {
                    commentCacheRepository.saveCommentCount(countCacheKey, count);
                    return count;
                } else {
                    commentCacheRepository.saveCommentCount(countCacheKey, 0L);
                    return 0L;
                }
            });
        }

        if (totalChildComments == 0L) {
            return new PageDTO<>();
        }

        // 尝试从缓存获取子评论列表
        List<CommentDTO> cachedComments = commentCacheRepository.getChildComments(
                query.getArticleId(), query.getCommentId(), query.getPage(), query.getPageSize());

        if (cachedComments != null) {
            // 缓存命中，直接返回
            return PageDTO.<CommentDTO>builder()
                    .total(totalChildComments)
                    .data(cachedComments)
                    .page(query.getPage())
                    .pageSize(query.getPageSize())
                    .build();
        }

        // 缓存未命中，使用分布式锁防止缓存击穿
        String listLockKey = String.format(RedisConstant.COMMENT_CHILD_LIST_LOCK, 
                query.getArticleId(), query.getCommentId(), query.getPage(), query.getPageSize());
                
        // 确保totalChildComments是final或有效final
        final Long finalTotalChildComments = totalChildComments;
        return lockUtil.tryLockAndReturn(listLockKey, () -> {
            // 双重检查，避免重复查询数据库
            List<CommentDTO> doubleCheckCachedComments = commentCacheRepository.getChildComments(
                    query.getArticleId(), query.getCommentId(), query.getPage(), query.getPageSize());
                    
            if (doubleCheckCachedComments != null) {
                return PageDTO.<CommentDTO>builder()
                        .total(finalTotalChildComments)
                        .data(doubleCheckCachedComments)
                        .page(query.getPage())
                        .pageSize(query.getPageSize())
                        .build();
            }

            // 缓存未命中，从数据库获取
            List<CommentEntity> commentEntityList = commentOperateService.getChildCommentsPage(query);
            List<CommentDTO> commentDTOList = getFullCommentList(commentEntityList);

            // 存入缓存 - 即使为空列表也缓存，避免缓存穿透
            commentCacheRepository.saveChildComments(
                    query.getArticleId(), query.getCommentId(), query.getPage(), query.getPageSize(), commentDTOList);

            return PageDTO.<CommentDTO>builder()
                    .total(finalTotalChildComments)
                    .data(commentDTOList)
                    .page(query.getPage())
                    .pageSize(query.getPageSize())
                    .build();
        });
    }

    /**
     * 优化userComment方法，处理并发情况
     */
    public PageDTO<CommentUserDTO> userComment(CommentUserPageQuery query) {
        query.valid();
        
        // 构建用户评论锁键
        String countLockKey = String.format(RedisConstant.USER_COMMENT_COUNT_LOCK, query.getUserId());
        LockUtil lockUtil = new LockUtil(redissonClient);

        // 尝试从缓存获取用户评论总数
        String userCountKey = "user:" + query.getUserId();
        Long totalUserComments = commentCacheRepository.getCommentCount(userCountKey);
        if (totalUserComments == null) {
            // 缓存未命中，使用分布式锁防止缓存击穿
            // 双重检查，避免重复查询数据库
            // 缓存未命中，从数据库获取
            // 存入缓存 - 无论评论数是否为0都缓存
            totalUserComments = lockUtil.tryLockAndReturn(countLockKey, () -> {
                // 双重检查，避免重复查询数据库
                Long cachedTotal = commentCacheRepository.getCommentCount(userCountKey);
                if (cachedTotal != null) {
                    return cachedTotal;
                }

                // 缓存未命中，从数据库获取
                Long count = commentOperateService.countUserCommentsPage(query);
                // 存入缓存 - 无论评论数是否为0都缓存
                if (count != null) {
                    commentCacheRepository.saveCommentCount(userCountKey, count);
                    return count;
                } else {
                    commentCacheRepository.saveCommentCount(userCountKey, 0L);
                    return 0L;
                }
            });
        }

        if (totalUserComments == 0L) {
            return new PageDTO<>();
        }

        // 尝试从缓存获取用户评论列表
        List<CommentDTO> cachedComments = commentCacheRepository.getUserComments(
                query.getUserId(), query.getPage(), query.getPageSize());

        // 缓存未命中，使用分布式锁防止缓存击穿
        String listLockKey = String.format(RedisConstant.USER_COMMENT_LIST_LOCK, 
                query.getUserId(), query.getPage(), query.getPageSize());

        // 确保totalUserComments是final或有效final
        final Long finalTotalUserComments = totalUserComments;
        if (cachedComments == null || cachedComments.isEmpty()) {
            return lockUtil.tryLockAndReturn(listLockKey, () -> {
                // 双重检查，避免重复查询数据库
                List<CommentDTO> doubleCheckCachedComments = commentCacheRepository.getUserComments(
                        query.getUserId(), query.getPage(), query.getPageSize());
                
                if (doubleCheckCachedComments != null && !doubleCheckCachedComments.isEmpty()) {
                    return processUserComments(doubleCheckCachedComments, finalTotalUserComments, query);
                }
                
                // 缓存未命中，从数据库获取
                List<CommentEntity> commentEntityList = commentOperateService.getUserComment(query);
                List<CommentDTO> commentDTOList = getFullCommentList(commentEntityList);

                // 存入缓存 - 即使为空列表也缓存，避免缓存穿透
                commentCacheRepository.saveUserComments(
                        query.getUserId(), query.getPage(), query.getPageSize(), commentDTOList);

                return processUserComments(commentDTOList, finalTotalUserComments, query);
            });
        } else {
            // 缓存命中，处理用户评论
            return processUserComments(cachedComments, finalTotalUserComments, query);
        }
    }
    
    /**
     * 处理用户评论数据，获取文章标题信息
     */
    private PageDTO<CommentUserDTO> processUserComments(List<CommentDTO> commentDTOList, Long total, CommentUserPageQuery query) {
        List<String> articleIdList = commentDTOList.stream()
                .map(CommentDTO::getArticleId)
                .distinct().collect(Collectors.toList());

        Map<String, ArticleEntity> articleEntityMap = articleOperateService.getArticleByIds(articleIdList);

        List<CommentUserDTO> commentUserDTOList = new ArrayList<>();

        commentDTOList.forEach(commentDTO -> {
            String articleTitle = articleEntityMap
                    .getOrDefault(commentDTO.getArticleId(), new ArticleEntity())
                    .getArticleTitle();
            CommentUserDTO commentUserDTO = CommentUserDTO.fromCommentDTO(commentDTO, articleTitle);
            commentUserDTOList.add(commentUserDTO);
        });

        return PageDTO.<CommentUserDTO>builder()
                .total(total)
                .data(commentUserDTOList)
                .page(query.getPage())
                .pageSize(query.getPageSize())
                .build();
    }
}
