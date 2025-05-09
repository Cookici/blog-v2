package com.lrh.article.domain.service;

import com.lrh.article.application.cqe.label.*;
import com.lrh.article.domain.entity.LabelEntity;
import com.lrh.article.domain.repository.LabelOperateRepository;
import com.lrh.article.infrastructure.database.convertor.LabelConvertor;
import com.lrh.article.infrastructure.po.LabelPO;
import com.lrh.common.util.IdUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.domain.service
 * @ClassName: LabelOperateService
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/20 15:23
 */
@Service
public class LabelOperateService {

    private final LabelOperateRepository labelRepository;

    public LabelOperateService(LabelOperateRepository labelRepository) {
        this.labelRepository = labelRepository;
    }


    public List<LabelEntity> getLabelKinds() {
        List<LabelPO> labelPOList = labelRepository.getLabelKinds();
        return LabelConvertor.toListLabelEntityConvertor(labelPOList);
    }

    public List<LabelEntity> getLabelList() {
        List<LabelPO> labelPOList = labelRepository.getLabelList();
        return LabelConvertor.toListLabelEntityConvertor(labelPOList);
    }

    public void updateLabel(LabelUpdateCommand command) {
        labelRepository.update(command.getLabelId(), command.getLabelAlias(), command.getLabelName(), command.getLabelDescription());
    }

    public Long countLabel(String keyword) {
        return labelRepository.countLabel(keyword);
    }

    public List<LabelEntity> pageLabel(LabelPageQuery query) {
        List<LabelPO> labelPOList = labelRepository.pageLabel(query.getKeyword(), query.getLimit(), query.getOffset());
        return LabelConvertor.toListLabelEntityConvertor(labelPOList);
    }

    public void deleteLabel(LabelDeleteCommand command) {
        labelRepository.delete(command.getLabelId());
    }

    public void insertLabel(LabelInsertCommand command) {
        LabelPO labelPO = new LabelPO();
        labelPO.setLabelAlias(command.getLabelAlias());
        labelPO.setLabelName(command.getLabelName());
        labelPO.setLabelDescription(command.getLabelDescription());
        labelPO.setLabelId("label_"+ IdUtil.getUuid());
        labelRepository.insert(labelPO);
    }

    public void restoreLabel(LabelRestoreCommand command) {
        labelRepository.restore(command.getLabelId());
    }

    public Long count() {
        return labelRepository.count();
    }
}
