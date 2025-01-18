package com.lrh.article.application.cqe.article;

import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.application.cqe.article
 * @ClassName: ArticleNoLoginLikeCommand
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/17 17:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleNoLoginLikeCommand {
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
        boolean matches = Pattern.matches("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$"
                , remoteAddr);
        if (!matches) {
            throw new ValidException("校验失败");
        }
        this.ip = remoteAddr;
    }
}
