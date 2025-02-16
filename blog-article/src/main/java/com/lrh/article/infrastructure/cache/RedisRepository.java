package com.lrh.article.infrastructure.cache;

import com.lrh.article.constants.RedisConstant;
import com.lrh.article.domain.repository.ArticleCacheRepository;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.*;

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

    @Override
    public Boolean incrArticleLikeCount(String articleId, String ukId) {
        return redisTemplate.opsForHash().putIfAbsent(String.format(RedisConstant.ARTICLE_LIKE, articleId), ukId, 1);
    }

    @Override
    public Long getArticleLikeCount(String articleId) {
        return redisTemplate.opsForHash().size(String.format(RedisConstant.ARTICLE_LIKE, articleId));
    }

    @Override
    public Long getArticleViewCount(String articleId) {
        return redisTemplate.opsForHash().size(String.format(RedisConstant.ARTICLE_VIEW, articleId));
    }

    @Override
    public Map<String, Long> getArticleLikeCountBatch(List<String> articleIds) {
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String articleId : articleIds) {
                String key = String.format(RedisConstant.ARTICLE_LIKE, articleId);
                connection.hLen(key.getBytes());
            }
            return null; // The return value is ignored
        });

        Map<String, Long> result = new HashMap<>();
        for (int i = 0; i < articleIds.size(); i++) {
            String articleId = articleIds.get(i);
            Long count = (Long) results.get(i);
            result.put(articleId, count);
        }
        return result;
    }

    @Override
    public Map<String, Long> getArticleViewCountBatch(List<String> articleIds) {
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String articleId : articleIds) {
                String key = String.format(RedisConstant.ARTICLE_VIEW, articleId);
                connection.hLen(key.getBytes());
            }
            return null; // The return value is ignored
        });

        Map<String, Long> result = new HashMap<>();
        for (int i = 0; i < articleIds.size(); i++) {
            String articleId = articleIds.get(i);
            Long count = (Long) results.get(i);
            result.put(articleId, count);
        }
        return result;
    }

    @Override
    public void deleteArticleCache(String articleId) {
        redisTemplate.delete(String.format(RedisConstant.ARTICLE_VIEW, articleId));
        redisTemplate.delete(String.format(RedisConstant.ARTICLE_LIKE, articleId));
    }

    @Override
    public Boolean deleteArticleLike(String articleId, String ukId) {
        String key = String.format(RedisConstant.ARTICLE_LIKE, articleId);
        String luaScript =
                "if redis.call('HEXISTS', KEYS[1], ARGV[1]) == 1 then " +
                        "   return redis.call('HDEL', KEYS[1], ARGV[1]); " +
                        "else " +
                        "   return 0; " +
                        "end";
        List<String> keys = Collections.singletonList(key);
        Long result = redisTemplate.execute(
                new DefaultRedisScript<>(luaScript, Long.class),
                keys,
                ukId
        );
        return result == 1;
    }
}
