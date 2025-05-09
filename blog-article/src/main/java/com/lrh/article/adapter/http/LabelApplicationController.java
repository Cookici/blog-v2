package com.lrh.article.adapter.http;

import com.lrh.article.application.cqe.label.*;
import com.lrh.article.application.dto.PageDTO;
import com.lrh.article.application.dto.label.LabelAdminDTO;
import com.lrh.article.application.dto.label.LabelDTO;
import com.lrh.article.application.service.LabelApplicationService;
import com.lrh.common.result.Result;
import org.springframework.web.bind.annotation.*;

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
        List<LabelDTO> labelDTOList = labelApplicationService.getLabelKinds();
        return Result.success(labelDTOList);
    }

    @GetMapping("/page/admin")
    public Result<PageDTO<LabelAdminDTO>> pageLabelAdmin(LabelPageQuery query) {
        PageDTO<LabelAdminDTO> resp = labelApplicationService.pageLabelAdmin(query);
        return Result.success(resp);
    }

    @PostMapping("/update")
    public Result<Object> updateLabel(@RequestBody LabelUpdateCommand command) {
        labelApplicationService.updateLabel(command);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<Object> deleteLabel(@RequestBody LabelDeleteCommand command){
        labelApplicationService.deleteLabel(command);
        return Result.success();
    }

    @PostMapping("/insert")
    public Result<Object> insertLabel(@RequestBody LabelInsertCommand command){
        labelApplicationService.insertLabel(command);
        return Result.success();
    }

    @PostMapping("/restore")
    public Result<Object> restoreLabel(@RequestBody LabelRestoreCommand command){
        labelApplicationService.restoreLabel(command);
        return Result.success();
    }

    @GetMapping("/count")
    public Result<Long> countLabel(){
        return Result.success(labelApplicationService.count());
    }


}
