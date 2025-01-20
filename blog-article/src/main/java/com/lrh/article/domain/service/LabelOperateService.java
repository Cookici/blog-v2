package com.lrh.article.domain.service;

import com.lrh.article.domain.entity.LabelEntity;
import com.lrh.article.domain.repository.LabelOperateRepository;
import com.lrh.article.infrastructure.database.convertor.LabelConvertor;
import com.lrh.article.infrastructure.po.LabelPO;
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
}
