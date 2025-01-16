package com.lrh.article.infrastructure.cache;

import com.lrh.article.constants.RedisConstant;
import com.lrh.article.domain.repository.ArticleCacheRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.infrastructure.cache
 * @ClassName: RedisService
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/15 17:43
 */
@Repository
public class RedisRepository implements ArticleCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void incrArticleViewCount(String articleId, String ukId) {
        //只需要统计人数 浏览次数后续可以用来做推荐
        redisTemplate.opsForHash().increment(String.format(RedisConstant.ARTICLE_VIEW, articleId), ukId, 1);
    }
}
