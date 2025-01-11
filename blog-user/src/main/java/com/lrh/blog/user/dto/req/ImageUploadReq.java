package com.lrh.blog.user.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageUploadReq {
    // 图片文件
    private MultipartFile imageFile;

    private String ip;
}
