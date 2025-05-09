package com.lrh.article.application.service;

import com.lrh.article.application.cqe.label.*;
import com.lrh.article.application.dto.PageDTO;
import com.lrh.article.application.dto.label.LabelAdminDTO;
import com.lrh.article.application.dto.label.LabelDTO;
import com.lrh.article.domain.entity.LabelEntity;
import com.lrh.article.domain.repository.ArticleLabelOperateRepository;
import com.lrh.article.domain.service.LabelOperateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.application.service
 * @ClassName: LabelApplicationService
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/20 15:20
 */
@Service
public class LabelApplicationService {

    private final LabelOperateService labelRepository;

    private final ArticleLabelOperateRepository articleLabelOperateRepository;


    public LabelApplicationService(LabelOperateService labelRepository,
                                   ArticleLabelOperateRepository articleLabelOperateRepository) {
        this.labelRepository = labelRepository;
        this.articleLabelOperateRepository = articleLabelOperateRepository;
    }


    public List<LabelDTO> getLabelKinds() {
        List<LabelEntity> labelEntityList = labelRepository.getLabelKinds();
        return LabelDTO.fromEntityList(labelEntityList);
    }

    public void updateLabel(LabelUpdateCommand command) {
        command.valid();
        labelRepository.updateLabel(command);
    }

    public PageDTO<LabelAdminDTO> pageLabelAdmin(LabelPageQuery query) {
        Long total = labelRepository.countLabel(query.getKeyword());

        if (total == null || total == 0) {
            return new PageDTO<>();
        }

        List<LabelEntity> labelEntityList =
                labelRepository.pageLabel(query);
        List<LabelAdminDTO> LabelAdminDTOList = LabelAdminDTO.fromEntityList(labelEntityList);
        return PageDTO.<LabelAdminDTO>builder()
                .total(total)
                .data(LabelAdminDTOList)
                .page(query.getPage())
                .pageSize(query.getPageSize())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteLabel(LabelDeleteCommand command) {
        command.valid();
        articleLabelOperateRepository.deleteArticleByLabel(command.getLabelId());
        labelRepository.deleteLabel(command);
    }

    public void insertLabel(LabelInsertCommand command) {
        command.valid();
        labelRepository.insertLabel(command);
    }

    public void restoreLabel(LabelRestoreCommand command) {
        command.valid();
        labelRepository.restoreLabel(command);
    }
}
