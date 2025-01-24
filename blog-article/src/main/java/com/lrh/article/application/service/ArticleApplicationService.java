package com.lrh.article.application.service;

import com.lrh.article.application.cqe.article.*;
import com.lrh.article.application.dto.PageDTO;
import com.lrh.article.application.dto.UserDataDTO;
import com.lrh.article.application.dto.article.ArticleDTO;
import com.lrh.article.constants.RedisConstant;
import com.lrh.article.domain.entity.ArticleEntity;
import com.lrh.article.domain.entity.UserArticleDataEntity;
import com.lrh.article.domain.repository.ArticleCacheRepository;
import com.lrh.article.domain.service.ArticleOperateService;
import com.lrh.article.domain.service.CommentOperateService;
import com.lrh.article.domain.vo.UserVO;
import com.lrh.article.infrastructure.client.MessageNettyClient;
import com.lrh.article.infrastructure.client.UserClient;
import com.lrh.article.util.LockUtil;
import com.lrh.common.context.UserContext;
import com.lrh.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final MessageNettyClient messageNettyClient;
    private RedissonClient redissonClient;

    public ArticleApplicationService(ArticleOperateService articleOperateService, ArticleCacheRepository articleCacheRepository,
                                     CommentOperateService commentOperateService, UserClient userClient,
                                     MessageNettyClient messageNettyClient) {
        this.articleOperateService = articleOperateService;
        this.articleCacheRepository = articleCacheRepository;
        this.commentOperateService = commentOperateService;
        this.userClient = userClient;
        this.messageNettyClient = messageNettyClient;
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
        articleOperateService.deleteArticleById(command);
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
        Result<Long> friendApplyCountResult = messageNettyClient.getFriendApplyCount(userId);
        Long friendApplyCount = friendApplyCountResult.getData();
        return UserDataDTO.fromEntity(userArticleData, commentCount, friendApplyCount);
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

}
