package com.lrh.article.application.cqe.article;

import com.lrh.article.util.CommonUtil;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.application.cqe.article
 * @ClassName: ArticleNoLoginViewCommand
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/17 17:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleNoLoginViewCommand {
    private String articleId;

    private HttpServletRequest httpServletRequest;

    private String ip;

    public void valid() {
        if (articleId == null) {
            throw new ValidException("校验失败");
        }
        if (articleId.length() > BusinessConstant.ID_MAX_LENGTH) {
            throw new ValidException("校验失败");
        }
        if (httpServletRequest == null) {
            throw new ValidException("校验失败");
        }

        String remoteAddr = httpServletRequest.getRemoteAddr();
        if (remoteAddr == null) {
            throw new ValidException("校验失败");
        }

        if (!CommonUtil.judgeIp(remoteAddr)) {
            throw new ValidException("校验失败");
        }
        this.ip = remoteAddr;
    }
}
