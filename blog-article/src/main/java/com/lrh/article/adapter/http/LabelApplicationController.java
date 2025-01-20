package com.lrh.article.adapter.http;

import com.lrh.article.application.dto.label.LabelDTO;
import com.lrh.article.application.service.LabelApplicationService;
import com.lrh.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.adapter.http
 * @ClassName: LabelApplicationController
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/20 15:18
 */
@RestController
@RequestMapping("/api/label")
public class LabelApplicationController {

    private final LabelApplicationService labelApplicationService;

    public LabelApplicationController(LabelApplicationService labelApplicationService) {
        this.labelApplicationService = labelApplicationService;
    }

    @GetMapping("/kinds")
    public Result<List<LabelDTO>> getLabelKinds() {
        List<LabelDTO> labelDTOList =  labelApplicationService.getLabelKinds();
        return Result.success(labelDTOList);
    }

}
