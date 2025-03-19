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
     * 文章数据库与ES每日增量对账锁
     */
    public static final String ARTICLE_RECONCILIATION_LOCK = "article:reconciliation:lock";
    
    /**
     * 文章数据库与ES全量对账锁
     */
    public static final String ARTICLE_FULL_RECONCILIATION_LOCK = "article:full:reconciliation:lock";

    /**
     * 评论缓存相关常量 - 修改为通用格式
     * 评论相关的分布式锁常量
     */
    public static final String COMMENT_LOCK_ARTICLE = "comment:lock:article:%s";
    public static final String COMMENT_LOCK_PARENT = "comment:lock:parent:%s";
    public static final String COMMENT_LOCK_COMMENT = "comment:lock:comment:%s";
    

    public static final String COMMENT_COUNT_LOCK = "comment:lock:count:%s";
    public static final String COMMENT_LIST_LOCK = "comment:lock:list:%s:%d:%d";
    public static final String COMMENT_CHILD_COUNT_LOCK = "comment:lock:child:count:%s:%s";
    public static final String COMMENT_CHILD_LIST_LOCK = "comment:lock:child:list:%s:%s:%d:%d";
    public static final String USER_COMMENT_COUNT_LOCK = "user:comment:lock:count:%s";
    public static final String USER_COMMENT_LIST_LOCK = "user:comment:lock:list:%s:%d:%d";

    /**
     * 评论缓存相关常量
     */
    public static final String COMMENT_COUNT = "comment:count:%s";
    public static final String ARTICLE_TOP_COMMENTS = "article:comment:top:%s:%d:%d";
    public static final String ARTICLE_TOP_COMMENTS_PATTERN = "article:comment:top:%s:*";
    public static final String ARTICLE_CHILD_COMMENTS = "article:comment:child:%s:%s:%d:%d";
    public static final String ARTICLE_CHILD_COMMENTS_PATTERN = "article:comment:child:%s:*";
    public static final String ARTICLE_CHILD_COMMENTS_BY_PARENT_PATTERN = "article:comment:child:%s:%s:*";
    public static final String USER_COMMENTS = "user:comment:%s:%d:%d";
    public static final String USER_COMMENTS_PATTERN = "user:comment:%s:*";


    /**
     * 子评论计数缓存键
     */
    public static final String COMMENT_CHILD_COUNT = "child:%s:%s";
}
