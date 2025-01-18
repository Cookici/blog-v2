package com.lrh.article.constants;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.constants
 * @ClassName: RedisConstant
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/25 16:35
 */

public class RedisConstant {
    public static final String PARENT_COMMENT_ID_OPERATOR_LOCK = "lock:article:operator:parent_comment_id_%s";
    public static final String ARTICLE_VIEW = "article:view:article_%s";
    public static final String ARTICLE_LIKE = "article:like:article_%s";
}
