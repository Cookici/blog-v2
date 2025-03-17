package com.lrh.article.application.service;

import com.lrh.article.application.cqe.article.*;
import com.lrh.article.application.dto.PageDTO;
import com.lrh.article.application.dto.UserDataDTO;
import com.lrh.article.application.dto.article.ArticleDTO;
import com.lrh.article.constants.RedisConstant;
import com.lrh.article.domain.entity.ArticleEntity;
import com.lrh.article.domain.entity.UserArticleDataEntity;
import com.lrh.article.domain.repository.ArticleCacheRepository;
import com.lrh.article.domain.repository.ArticleLikeRepository;
import com.lrh.article.domain.service.ArticleOperateService;
import com.lrh.article.domain.service.CommentOperateService;
import com.lrh.article.domain.vo.UserVO;
import com.lrh.article.infrastructure.client.UserClient;
import com.lrh.article.util.LockUtil;
import com.lrh.common.context.UserContext;
import com.lrh.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.application.service
 * @ClassName: ArticleService
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/14 20:56
 */

@Slf4j
@Service
public class ArticleApplicationService {
    private final ArticleOperateService articleOperateService;
    private final ArticleCacheRepository articleCacheRepository;
    private final CommentOperateService commentOperateService;
    private final UserClient userClient;
    private final RedissonClient redissonClient;
    private final ArticleLikeRepository articleLikeRepository;

    public ArticleApplicationService(ArticleOperateService articleOperateService, ArticleCacheRepository articleCacheRepository,
                                     CommentOperateService commentOperateService, UserClient userClient,
                                     RedissonClient redissonClient, ArticleLikeRepository articleLikeRepository) {
        this.articleOperateService = articleOperateService;
        this.articleCacheRepository = articleCacheRepository;
        this.commentOperateService = commentOperateService;
        this.userClient = userClient;
        this.redissonClient = redissonClient;
        this.articleLikeRepository = articleLikeRepository;
    }

    public PageDTO<ArticleDTO> pageArticles(ArticlePageQuery query) {
        query.valid();
        Long total = articleOperateService.countArticlesPage(query);
        if (total == null || total == 0) {
            return new PageDTO<>();
        }
        List<ArticleEntity> articleEntityList = articleOperateService.getArticlesPage(query);
        List<String> userIds = articleEntityList.stream().map(ArticleEntity::getUserId).collect(Collectors.toList());

        Result<Map<String, UserVO>> userList = userClient.getByIds(userIds);
        Map<String, UserVO> userIdForUser = userList.getData();

        List<ArticleDTO> articleDTOList = new ArrayList<>();
        articleEntityList.forEach(articleEntity -> {
                    UserVO userVO = userIdForUser.get(articleEntity.getUserId());
                    if (userVO != null) {
                        articleDTOList.add(ArticleDTO.fromEntity(articleEntity, userVO));
                    }
                }
        );

        List<String> articleIds = articleEntityList.stream().map(ArticleEntity::getArticleId).collect(Collectors.toList());
        Map<String, Long> articleLikeCountBatch = articleCacheRepository.getArticleLikeCountBatch(articleIds);
        Map<String, Long> articleViewCountBatch = articleCacheRepository.getArticleViewCountBatch(articleIds);

        articleDTOList.forEach(articleDTO -> {
            Long likeCount = articleLikeCountBatch.getOrDefault(articleDTO.getArticleId(), 0L);
            Long viewCount = articleViewCountBatch.getOrDefault(articleDTO.getArticleId(), 0L);
            articleDTO.setLikeCount(likeCount);
            articleDTO.setViewCount(viewCount);
        });


        return PageDTO.<ArticleDTO>builder()
                .page(query.getPage())
                .total(total)
                .pageSize(query.getPageSize())
                .data(articleDTOList).
                build();
    }

    public ArticleDTO getArticleById(ArticleQuery query) {
        query.valid();
        LockUtil lockUtil = new LockUtil(redissonClient);
        return lockUtil.tryReadLock(String.format(RedisConstant.ARTICLE_LOCK, query.getArticleId()), () -> {
            ArticleEntity articleEntity = articleOperateService.getArticleById(query);
            if (articleEntity == null) {
                return new ArticleDTO();
            }
            List<String> userIdList = new ArrayList<>();
            userIdList.add(articleEntity.getUserId());
            Result<Map<String, UserVO>> userList = userClient.getByIds(userIdList);
            Map<String, UserVO> userIdForUser = userList.getData();
            UserVO userInfo = userIdForUser.get(articleEntity.getUserId());
            if (userInfo == null) {
                userInfo = new UserVO();
            }

            ArticleDTO articleDTO = ArticleDTO.fromEntity(articleEntity, userInfo);

            Long articleViewCount = articleCacheRepository.getArticleViewCount(articleDTO.getArticleId());
            Long articleLikeCount = articleCacheRepository.getArticleLikeCount(articleDTO.getArticleId());
            articleDTO.setLikeCount(articleLikeCount);
            articleDTO.setViewCount(articleViewCount);

            return articleDTO;
        });
    }

    public void deleteArticleById(ArticleDeleteCommand command) {
        command.valid();
        articleOperateService.deleteById(command);
    }

    public void updateArticle(ArticleUpdateCommand command) {
        command.valid();
        articleOperateService.updateArticleById(command);
    }

    public void insertArticle(ArticleInsertCommand command) {
        command.valid();
        articleOperateService.insertArticle(command);
    }

    public void articleViewIncrement(ArticleViewCommand command) {
        command.valid();
        articleOperateService.articleViewIncrement(command);
    }

    public void articleLikeIncrement(ArticleLikeCommand command) {
        command.valid();
        articleOperateService.articleLikeIncrement(command);
    }

    public void articleNoLoginViewIncrement(ArticleNoLoginViewCommand command) {
        command.valid();
        articleOperateService.articleNoLoginViewIncrement(command);
    }

    public void articleNoLoginLikeIncrement(ArticleNoLoginLikeCommand command) {
        command.valid();
        articleOperateService.articleNoLoginLikeIncrement(command);
    }

    public UserDataDTO articlesDataByUserId() {
        String userId = UserContext.getUserId();
        UserArticleDataEntity userArticleData =
                articleOperateService.articlesDataByUserId(userId);
        Long commentCount = commentOperateService.getUserCommentAsTo(userId);
        return UserDataDTO.fromEntity(userArticleData, commentCount);
    }


    public PageDTO<ArticleDTO> pageUserArticles(ArticleUserPageQuery query) {
        query.valid();
        Long total = articleOperateService.countUserArticlesPage(query);
        if (total == null || total == 0) {
            return new PageDTO<>();
        }
        List<ArticleEntity> articleEntityList = articleOperateService.getUserArticlesPage(query);
        List<String> userIds = articleEntityList.stream().map(ArticleEntity::getUserId).collect(Collectors.toList());

        Result<Map<String, UserVO>> userList = userClient.getByIds(userIds);
        Map<String, UserVO> userIdForUser = userList.getData();

        List<ArticleDTO> articleDTOList = new ArrayList<>();
        articleEntityList.forEach(articleEntity -> {
                    UserVO userVO = userIdForUser.get(articleEntity.getUserId());
                    if (userVO != null) {
                        articleDTOList.add(ArticleDTO.fromEntity(articleEntity, userVO));
                    }
                }
        );

        List<String> articleIds = articleEntityList.stream().map(ArticleEntity::getArticleId).collect(Collectors.toList());
        Map<String, Long> articleLikeCountBatch = articleCacheRepository.getArticleLikeCountBatch(articleIds);
        Map<String, Long> articleViewCountBatch = articleCacheRepository.getArticleViewCountBatch(articleIds);

        articleDTOList.forEach(articleDTO -> {
            Long likeCount = articleLikeCountBatch.getOrDefault(articleDTO.getArticleId(), 0L);
            Long viewCount = articleViewCountBatch.getOrDefault(articleDTO.getArticleId(), 0L);
            articleDTO.setLikeCount(likeCount);
            articleDTO.setViewCount(viewCount);
        });


        return PageDTO.<ArticleDTO>builder()
                .page(query.getPage())
                .total(total)
                .pageSize(query.getPageSize())
                .data(articleDTOList).
                build();
    }

    public PageDTO<ArticleDTO> listQueryArticles(ArticleListQuery query) {
        query.valid();

        Long total = articleOperateService.countArticlesEsPage(query);
        if (total == null || total == 0) {
            return new PageDTO<>();
        }
        List<ArticleEntity> articleEntityList = articleOperateService.getArticlesEsPage(query);
        List<String> userIds = articleEntityList.stream().map(ArticleEntity::getUserId).collect(Collectors.toList());

        Result<Map<String, UserVO>> userList = userClient.getByIds(userIds);
        Map<String, UserVO> userIdForUser = userList.getData();
        List<ArticleDTO> articleDTOList = new ArrayList<>();
        articleEntityList.forEach(articleEntity -> {
                    UserVO userVO = userIdForUser.get(articleEntity.getUserId());
                    if (userVO != null) {
                        articleDTOList.add(ArticleDTO.fromEntity(articleEntity, userVO));
                    }
                }
        );

        List<String> articleIds = articleEntityList.stream().map(ArticleEntity::getArticleId).collect(Collectors.toList());
        Map<String, Long> articleLikeCountBatch = articleCacheRepository.getArticleLikeCountBatch(articleIds);
        Map<String, Long> articleViewCountBatch = articleCacheRepository.getArticleViewCountBatch(articleIds);

        articleDTOList.forEach(articleDTO -> {
            Long likeCount = articleLikeCountBatch.getOrDefault(articleDTO.getArticleId(), 0L);
            Long viewCount = articleViewCountBatch.getOrDefault(articleDTO.getArticleId(), 0L);
            articleDTO.setLikeCount(likeCount);
            articleDTO.setViewCount(viewCount);
        });


        return PageDTO.<ArticleDTO>builder()
                .page(query.getPage())
                .total(total)
                .pageSize(query.getPageSize())
                .data(articleDTOList).
                build();
    }

    public void deleteLike(ArticleDeleteLikeCommand command) {
        command.valid();
        articleOperateService.deleteArticleLike(command.getArticleId(), UserContext.getUserId());
    }

    public List<ArticleDTO> recommendArticles(ArticleRecommendQuery query) {
        query.valid();
        String userId = query.getUserId();
        
        // 先从缓存获取推荐文章
        List<ArticleDTO> result = articleCacheRepository.getUserRecommendArticles(userId);
        if(result != null && !result.isEmpty()) {
            return result;
        }
        
        // 缓存未命中，生成推荐并缓存
        List<ArticleDTO> recommendArticles = generateRecommendArticles(userId);
        
        // 异步保存到缓存
        if (recommendArticles != null && !recommendArticles.isEmpty()) {
            saveUserRecommendCache(userId, recommendArticles);
        }
        
        return recommendArticles;
    }
    
    /**
     * 生成用户推荐文章
     * 拆分出核心推荐逻辑
     */
    public List<ArticleDTO> generateRecommendArticles(String userId) {
        // 获取推荐文章列表
        List<ArticleEntity> articleEntityList = articleOperateService.getRecommendArticles(userId);
        
        if (articleEntityList == null || articleEntityList.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取文章作者信息
        List<String> userIds = articleEntityList.stream()
                .map(ArticleEntity::getUserId)
                .collect(Collectors.toList());
        
        Result<Map<String, UserVO>> userList = userClient.getByIds(userIds);
        Map<String, UserVO> userIdForUser = userList.getData();
        
        // 组装文章DTO
        List<ArticleDTO> articleDTOList = new ArrayList<>();
        articleEntityList.forEach(articleEntity -> {
            UserVO userVO = userIdForUser.get(articleEntity.getUserId());
            if (userVO != null) {
                articleDTOList.add(ArticleDTO.fromEntity(articleEntity, userVO));
            }
        });
        
        // 获取文章点赞和浏览数
        List<String> articleIds = articleEntityList.stream()
                .map(ArticleEntity::getArticleId)
                .collect(Collectors.toList());
                
        Map<String, Long> articleLikeCountBatch = articleCacheRepository.getArticleLikeCountBatch(articleIds);
        Map<String, Long> articleViewCountBatch = articleCacheRepository.getArticleViewCountBatch(articleIds);
        
        articleDTOList.forEach(articleDTO -> {
            Long likeCount = articleLikeCountBatch.getOrDefault(articleDTO.getArticleId(), 0L);
            Long viewCount = articleViewCountBatch.getOrDefault(articleDTO.getArticleId(), 0L);
            articleDTO.setLikeCount(likeCount);
            articleDTO.setViewCount(viewCount);
        });
        
        // 限制返回10条记录
        if (articleDTOList.size() > 10) {
            return articleDTOList.subList(0, 10);
        }
        
        return articleDTOList;
    }

    @Async("articleAsyncExecutor")
    protected void saveUserRecommendCache(String userId, List<ArticleDTO> articleDTOList) {
        articleCacheRepository.saveUserRecommendCache(userId, articleDTOList);
    }


    public PageDTO<ArticleDTO> likeArticlesPage(ArticleLikePageQuery query) {
        query.valid();
        Set<String> likeArticleIds = articleLikeRepository.getLikedArticleIdsByUserId(query.getUserId());
        if (likeArticleIds == null || likeArticleIds.isEmpty()) {
            return new PageDTO<>();
        }
        Long total = articleOperateService.countLikeArticlesPage(query, likeArticleIds);
        if (total == null || total == 0) {
            return new PageDTO<>();
        }
        List<ArticleEntity> articleEntityList = articleOperateService.getLikeArticlesPage(query, likeArticleIds);

        List<String> userIds = articleEntityList.stream().map(ArticleEntity::getUserId).collect(Collectors.toList());

        Result<Map<String, UserVO>> userList = userClient.getByIds(userIds);
        Map<String, UserVO> userIdForUser = userList.getData();
        List<ArticleDTO> articleDTOList = new ArrayList<>();
        articleEntityList.forEach(articleEntity -> {
                    UserVO userVO = userIdForUser.get(articleEntity.getUserId());
                    if (userVO != null) {
                        articleDTOList.add(ArticleDTO.fromEntity(articleEntity, userVO));
                    }
                }
        );

        List<String> articleIds = articleEntityList.stream().map(ArticleEntity::getArticleId).collect(Collectors.toList());
        Map<String, Long> articleLikeCountBatch = articleCacheRepository.getArticleLikeCountBatch(articleIds);
        Map<String, Long> articleViewCountBatch = articleCacheRepository.getArticleViewCountBatch(articleIds);

        articleDTOList.forEach(articleDTO -> {
            Long likeCount = articleLikeCountBatch.getOrDefault(articleDTO.getArticleId(), 0L);
            Long viewCount = articleViewCountBatch.getOrDefault(articleDTO.getArticleId(), 0L);
            articleDTO.setLikeCount(likeCount);
            articleDTO.setViewCount(viewCount);
        });

        return PageDTO.<ArticleDTO>builder()
                .page(query.getPage())
                .total(total)
                .pageSize(query.getPageSize())
                .data(articleDTOList)
                .build();
    }

    public PageDTO<ArticleDTO> listQueryUserArticles(ArticleEsUserPageQuery query) {
        query.valid();
        Long total = articleOperateService.countEsUserArticlesEsPage(query);
        if (total == null || total == 0) {
            return new PageDTO<>();
        }
        List<ArticleEntity> articleEntityList = articleOperateService.getEsUserArticlesEsPage(query);
        List<String> userIds = articleEntityList.stream().map(ArticleEntity::getUserId).collect(Collectors.toList());

        Result<Map<String, UserVO>> userList = userClient.getByIds(userIds);
        Map<String, UserVO> userIdForUser = userList.getData();

        List<ArticleDTO> articleDTOList = new ArrayList<>();
        articleEntityList.forEach(articleEntity -> {
            UserVO userVO = userIdForUser.get(articleEntity.getUserId());
            if (userVO != null) {
                articleDTOList.add(ArticleDTO.fromEntity(articleEntity, userVO));
            }
        });

        List<String> articleIds = articleEntityList.stream().map(ArticleEntity::getArticleId).collect(Collectors.toList());
        Map<String, Long> articleLikeCountBatch = articleCacheRepository.getArticleLikeCountBatch(articleIds);
        Map<String, Long> articleViewCountBatch = articleCacheRepository.getArticleViewCountBatch(articleIds);

        articleDTOList.forEach(articleDTO -> {
            Long likeCount = articleLikeCountBatch.getOrDefault(articleDTO.getArticleId(), 0L);
            Long viewCount = articleViewCountBatch.getOrDefault(articleDTO.getArticleId(), 0L);
            articleDTO.setLikeCount(likeCount);
            articleDTO.setViewCount(viewCount);
        });

        return PageDTO.<ArticleDTO>builder()
                .page(query.getPage())
                .total(total)
                .pageSize(query.getPageSize())
                .data(articleDTOList)
                .build();
    }

    public List<ArticleDTO> getHotArticles(ArticleHotQuery query) {
        query.valid();
        List<ArticleEntity> articleEntityList = articleOperateService.getHotArticles(query);
        if (articleEntityList == null || articleEntityList.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> userIds = articleEntityList.stream().map(ArticleEntity::getUserId).collect(Collectors.toList());

        Result<Map<String, UserVO>> userList = userClient.getByIds(userIds);
        Map<String, UserVO> userIdForUser = userList.getData();

        List<ArticleDTO> articleDTOList = new ArrayList<>();
        articleEntityList.forEach(articleEntity -> {
            UserVO userVO = userIdForUser.get(articleEntity.getUserId());
            if (userVO != null) {
                articleDTOList.add(ArticleDTO.fromEntity(articleEntity, userVO));
            }
        });

        List<String> articleIds = articleEntityList.stream().map(ArticleEntity::getArticleId).collect(Collectors.toList());
        Map<String, Long> articleLikeCountBatch = articleCacheRepository.getArticleLikeCountBatch(articleIds);
        Map<String, Long> articleViewCountBatch = articleCacheRepository.getArticleViewCountBatch(articleIds);

        articleDTOList.forEach(articleDTO -> {
            Long likeCount = articleLikeCountBatch.getOrDefault(articleDTO.getArticleId(), 0L);
            Long viewCount = articleViewCountBatch.getOrDefault(articleDTO.getArticleId(), 0L);
            articleDTO.setLikeCount(likeCount);
            articleDTO.setViewCount(viewCount);
        });

        saveUserHotCache(query.getUserId(), articleIds);

        return articleDTOList;
    }

    @Async("articleAsyncExecutor")
    protected void saveUserHotCache(String userId, List<String> articleIds) {
        articleCacheRepository.saveUserHotArticleIds(userId, articleIds);
    }
}
