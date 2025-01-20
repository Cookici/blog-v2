package com.lrh.article.application.service;

import com.lrh.article.application.dto.label.LabelDTO;
import com.lrh.article.domain.entity.LabelEntity;
import com.lrh.article.domain.service.LabelOperateService;
import org.springframework.stereotype.Service;

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

    public LabelApplicationService(LabelOperateService labelRepository) {
        this.labelRepository = labelRepository;
    }


    public List<LabelDTO> getLabelKinds() {
        List<LabelEntity> labelEntityList = labelRepository.getLabelKinds();
        return LabelDTO.fromEntityList(labelEntityList);
    }
}
