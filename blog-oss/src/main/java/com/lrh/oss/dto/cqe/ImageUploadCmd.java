package com.lrh.oss.dto.cqe;

import com.lrh.oss.dto.req.ImageUploadReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageUploadCmd {
    private MultipartFile imageFile;

    private String ip;

    public ImageUploadCmd(ImageUploadReq req) {
        req.valid();
        this.imageFile = req.getImageFile();
        this.ip = req.getIp();
    }
}
