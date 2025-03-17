package com.lrh.article.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrh.article.application.dto.article.ArticleDTO;
import com.lrh.article.constants.RedisConstant;
import com.lrh.article.domain.repository.ArticleCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.article.infrastructure.cache
 * @ClassName: RedisService
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/15 17:43
 */

@Slf4j
@Repository
public class RedisRepository implements ArticleCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;


    public RedisRepository(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void incrArticleViewCount(String articleId, String ukId) {
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
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(String.format(RedisConstant.ARTICLE_VIEW, articleId));
        long count = 0;
        for (Object value : entries.values()) {
            if (value instanceof Integer) {
                count += (Integer) value;
            }
        }
        return count;
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
                connection.hVals(key.getBytes());
            }
            return null;
        });

        Map<String, Long> result = new HashMap<>();
        for (int i = 0; i < articleIds.size(); i++) {
            String articleId = articleIds.get(i);
            long count = 0;
            if (results.get(i) instanceof List<?>) {
                List<?> values = (List<?>) results.get(i);
                for (Object value : values) {
                    if (value instanceof Integer) {
                        count += (Integer) value;
                    }
                }
            }
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

    @Override
    public List<String> getUserHotArticleIds(String userId) {
        String key = String.format(RedisConstant.USER_ARTICLE_HOT_ID, userId);
        Set<Object> articleIds = redisTemplate.opsForSet().members(key);
        if (articleIds != null && !articleIds.isEmpty()) {
            return articleIds.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void saveUserHotArticleIds(String userId, List<String> articleIds) {
        if (userId == null || articleIds == null || articleIds.isEmpty()) {
            return;
        }
        String key = String.format(RedisConstant.USER_ARTICLE_HOT_ID, userId);
        redisTemplate.opsForSet().add(key, articleIds.toArray());
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    @Override
    public List<ArticleDTO> getUserRecommendArticles(String userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        String key = String.format(RedisConstant.USER_ARTICLE_RECOMMEND, userId);
        try {
            Object cachedData = redisTemplate.opsForValue().get(key);
            if (cachedData != null) {
                if (cachedData instanceof String) {
                    return objectMapper.readValue((String) cachedData, 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ArticleDTO.class));
                } else {
                    return objectMapper.convertValue(cachedData, 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ArticleDTO.class));
                }
            }
        } catch (Exception e) {
            log.error("[RedisRepository] getUserRecommendArticles error : {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public void saveUserRecommendCache(String userId, List<ArticleDTO> articleDTOList) {
        if (userId == null || articleDTOList == null || articleDTOList.isEmpty()) {
            return;
        }
        try {
            String key = String.format(RedisConstant.USER_ARTICLE_RECOMMEND, userId);
            String jsonValue = objectMapper.writeValueAsString(articleDTOList);
            redisTemplate.opsForValue().set(key, jsonValue, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("[RedisRepository] saveUserRecommendCache error : {} ", e.getMessage());
        }
    }
}
