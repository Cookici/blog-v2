package com.lrh.article.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrh.article.application.cqe.article.ArticlePageQuery;
import com.lrh.article.application.dto.PageDTO;
import com.lrh.article.application.dto.article.ArticleDTO;
import com.lrh.article.application.dto.article.ArticlePageDTO;
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

    public ArticlePageDTO pageArticles(ArticlePageQuery query) throws ValidException {
        try {
            query.valid();
        } catch (ValidException e) {
            log.error("[ArticleApplicationService] pageArticles error : {}",e.getMessage());
            throw new ValidException(e.getMessage());
        }

        Long total = articleOperateService.countArticlesPage(query);
        if(total == null || total == 0){
            return null;
        }
        Page<ArticleEntity> articleEntityPage = articleOperateService.getArticlesPage(query);
        List<ArticleDTO> articleDTOList = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        articleEntityPage.getRecords().forEach(articleEntity -> {
                    articleDTOList.add(ArticleDTO.fromEntity(articleEntity));
                    userIds.add(articleEntity.getUserId());
                }
        );
        Result<Map<String, UserVO>> userList = userClient.getByIds(userIds);
        Map<String, UserVO> userIdForUser = userList.getData();
        articleDTOList.forEach(articleDTO -> {
                    UserVO userVO = userIdForUser.get(articleDTO.getUserId());
                    if (userVO != null) {
                        articleDTO.setUserInfo(userVO);
                    }
                }
        );

        return new ArticlePageDTO(new PageDTO<>(articleDTOList, total,
                articleEntityPage.getCurrent(), articleEntityPage.getSize()));
    }
}
