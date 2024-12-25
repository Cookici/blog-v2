package com.lrh.oss.dto.req;

import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import com.lrh.oss.constant.OssConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageUploadReq {

    private MultipartFile imageFile;

    public void valid() {
        if (imageFile == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        String fileName = imageFile.getOriginalFilename();
        String sufFileName = fileName.substring(fileName.lastIndexOf("."));
        // 是否满足图片后缀名
        boolean ok = false;
        for (String value : OssConstant.IMAGES_TYPE) {
            if (value.equals(sufFileName)) {
                ok = true;
                break;
            }
        }
        if (!ok) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
    }
}
