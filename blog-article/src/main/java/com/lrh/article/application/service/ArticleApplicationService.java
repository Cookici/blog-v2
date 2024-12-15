package com.lrh.article.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrh.article.application.cqe.article.ArticlePageQuery;
import com.lrh.article.application.dto.PageDTO;
import com.lrh.article.application.dto.article.ArticleDTO;
import com.lrh.article.domain.entity.ArticleEntity;
import com.lrh.article.domain.service.ArticleOperateService;
import com.lrh.article.domain.vo.UserVO;
import com.lrh.article.infrastructure.client.UserClient;
import com.lrh.common.exception.ValidException;
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

    public PageDTO<ArticleDTO> pageArticles(ArticlePageQuery query) throws ValidException {
        try {
            query.valid();
        } catch (ValidException e) {
            log.error("[ArticleApplicationService] pageArticles error : {}", e.getMessage());
            throw new ValidException(e.getMessage());
        }

        Long total = articleOperateService.countArticlesPage(query);
        if (total == null || total == 0) {
            return null;
        }
        Page<ArticleEntity> articleEntityPage = articleOperateService.getArticlesPage(query);
        List<ArticleEntity> articleEntityList = articleEntityPage.getRecords();
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
                .page(articleEntityPage.getCurrent())
                .total(total)
                .pageSize(articleEntityPage.getSize())
                .data(articleDTOList).
                build();
    }
}
