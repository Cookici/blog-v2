package com.lrh.article.application.service;

import com.lrh.article.application.cqe.article.*;
import com.lrh.article.application.dto.PageDTO;
import com.lrh.article.application.dto.article.ArticleDTO;
import com.lrh.article.domain.entity.ArticleEntity;
import com.lrh.article.domain.service.ArticleOperateService;
import com.lrh.article.domain.vo.UserVO;
import com.lrh.article.infrastructure.client.UserClient;
import com.lrh.common.result.Result;
import lombok.extern.slf4j.Slf4j;
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

    private final UserClient userClient;

    public ArticleApplicationService(ArticleOperateService articleOperateService, UserClient userClient) {
        this.articleOperateService = articleOperateService;
        this.userClient = userClient;
    }

    public PageDTO<ArticleDTO> pageArticles(ArticlePageQuery query) {
        query.valid();
        Long total = articleOperateService.countArticlesPage(query);
        if (total == null || total == 0) {
            return null;
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

        return PageDTO.<ArticleDTO>builder()
                .page(query.getPage())
                .total(total)
                .pageSize(query.getPageSize())
                .data(articleDTOList).
                build();
    }

    public ArticleDTO getArticleById(ArticleQuery query) {
        query.valid();
        ArticleEntity articleEntity = articleOperateService.getArticleById(query);
        if (articleEntity == null) {
            return null;
        }
        List<String> userIdList = new ArrayList<>();
        userIdList.add(articleEntity.getUserId());
        Result<Map<String, UserVO>> userList = userClient.getByIds(userIdList);
        Map<String, UserVO> userIdForUser = userList.getData();
        UserVO userInfo = userIdForUser.get(articleEntity.getUserId());
        if (userInfo == null) {
            userInfo = new UserVO();
        }
        return ArticleDTO.fromEntity(articleEntity, userInfo);
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
}
