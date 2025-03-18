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
    public static final String ARTICLE_LOCK = "lock:article:operator:article_%s";
    /**
     * 文章指标数据同步锁
     */
    public static final String ARTICLE_METRICS_SYNC_LOCK = "lock:article:metrics:sync:";
    /**
     * 用户已经获取的hot文章
     */
    public static final String USER_ARTICLE_HOT_ID = "article:hot_%s";

    public static final String USER_ARTICLE_RECOMMEND = "user:article:recommend:%s";
    
    /**
     * 文章推荐预热锁
     */
    public static final String ARTICLE_RECOMMEND_PRELOAD_LOCK = "article:recommend:preload:lock";
    
    /**
     * 文章数据库与ES每日增量对账锁
     */
    public static final String ARTICLE_RECONCILIATION_LOCK = "article:reconciliation:lock";
    
    /**
     * 文章数据库与ES全量对账锁
     */
    public static final String ARTICLE_FULL_RECONCILIATION_LOCK = "article:full:reconciliation:lock";
}
