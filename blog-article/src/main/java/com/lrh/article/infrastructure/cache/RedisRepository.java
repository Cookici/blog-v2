package com.lrh.article.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lrh.article.application.dto.article.ArticleDTO;
import com.lrh.article.application.dto.comment.CommentDTO;
import com.lrh.article.constants.RedisConstant;
import com.lrh.article.domain.repository.ArticleCacheRepository;
import com.lrh.article.domain.repository.CommentCacheRepository;
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
public class RedisRepository implements ArticleCacheRepository, CommentCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;


    public RedisRepository(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void incrArticleViewCount(String articleId, String ukId) {
        redisTemplate.opsForValue().increment(String.format(RedisConstant.ARTICLE_VIEW, articleId), 1);
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
        long count = 0L;
        Object value = redisTemplate.opsForValue().get(String.format(RedisConstant.ARTICLE_VIEW, articleId));
        if (value != null) {
            if (value instanceof Integer) {
                count = ((Integer) value).longValue();
            } else if (value instanceof Long) {
                count = (Long) value;
            } else if (value instanceof String) {
                count = Long.parseLong((String) value);
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
                connection.get(key.getBytes());
            }
            return null;
        });

        Map<String, Long> result = new HashMap<>();
        for (int i = 0; i < articleIds.size(); i++) {
            String articleId = articleIds.get(i);
            long count = 0L;
            Object value = results.get(i);
            if (value != null) {
                if (value instanceof Integer) {
                    count = ((Integer) value).longValue();
                } else if (value instanceof Long) {
                    count = (Long) value;
                } else if (value instanceof String) {
                    count = Long.parseLong((String) value);
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

    @Override
    public void restoreArticleLikeAndView(String articleId, List<String> userIdList, Long viewCount) {
        if (viewCount != null) {
            String viewKey = String.format(RedisConstant.ARTICLE_VIEW, articleId);
            redisTemplate.opsForValue().set(viewKey, String.valueOf(viewCount));
        }

        String likeKey = String.format(RedisConstant.ARTICLE_LIKE, articleId);
        redisTemplate.delete(likeKey);
        Map<String, Integer> likeMap = new HashMap<>();
        for (String userId : userIdList) {
            if (userId != null && !userId.isEmpty()) {
                likeMap.put(userId, 1);
            }
        }
        redisTemplate.opsForHash().putAll(likeKey, likeMap);
    }

    @Override
    public void saveCommentCount(String key, Long count) {
        if (key == null || count == null) {
            return;
        }
        String cacheKey = String.format(RedisConstant.COMMENT_COUNT, key);
        redisTemplate.opsForValue().set(cacheKey, count, 1, TimeUnit.DAYS);
    }

    @Override
    public Long getCommentCount(String key) {
        if (key == null) {
            return 0L;
        }
        String cacheKey = String.format(RedisConstant.COMMENT_COUNT, key);
        Object value = redisTemplate.opsForValue().get(cacheKey);
        return value == null ? null : Long.valueOf(value.toString());
    }


    @Override
    public void saveTopComments(String articleId, Long page, Long pageSize, List<CommentDTO> comments) {
        if (articleId == null || comments == null) {
            return;
        }
        try {
            String key = String.format(RedisConstant.ARTICLE_TOP_COMMENTS, articleId, page, pageSize);
            String jsonValue = objectMapper.writeValueAsString(comments);
            redisTemplate.opsForValue().set(key, jsonValue, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("[RedisRepository] saveTopComments error : {}", e.getMessage(), e);
        }
    }

    @Override
    public List<CommentDTO> getTopComments(String articleId, Long page, Long pageSize) {
        if (articleId == null) {
            return null;
        }
        try {
            String key = String.format(RedisConstant.ARTICLE_TOP_COMMENTS, articleId, page, pageSize);
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            return objectMapper.readValue(value.toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CommentDTO.class));
        } catch (Exception e) {
            log.error("[RedisRepository] getTopComments error : {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void saveChildComments(String articleId, String parentCommentId, Long page, Long pageSize, List<CommentDTO> comments) {
        if (articleId == null || parentCommentId == null || comments == null) {
            return;
        }
        try {
            String key = String.format(RedisConstant.ARTICLE_CHILD_COMMENTS, articleId, parentCommentId, page, pageSize);
            String jsonValue = objectMapper.writeValueAsString(comments);
            redisTemplate.opsForValue().set(key, jsonValue, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("[RedisRepository] saveChildComments error : {}", e.getMessage(), e);
        }
    }

    @Override
    public List<CommentDTO> getChildComments(String articleId, String parentCommentId, Long page, Long pageSize) {
        if (articleId == null || parentCommentId == null) {
            return null;
        }
        try {
            String key = String.format(RedisConstant.ARTICLE_CHILD_COMMENTS, articleId, parentCommentId, page, pageSize);
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            return objectMapper.readValue(value.toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CommentDTO.class));
        } catch (Exception e) {
            log.error("[RedisRepository] getChildComments error : {}", e.getMessage(), e);
            return null;
        }
    }

    // 用户评论缓存
    @Override
    public void saveUserComments(String userId, Long page, Long pageSize, List<CommentDTO> comments) {
        if (userId == null || comments == null) {
            return;
        }
        try {
            String key = String.format(RedisConstant.USER_COMMENTS, userId, page, pageSize);
            String jsonValue = objectMapper.writeValueAsString(comments);
            redisTemplate.opsForValue().set(key, jsonValue, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("[RedisRepository] saveUserComments error : {}", e.getMessage(), e);
        }
    }

    @Override
    public List<CommentDTO> getUserComments(String userId, Long page, Long pageSize) {
        if (userId == null) {
            return null;
        }
        try {
            String key = String.format(RedisConstant.USER_COMMENTS, userId, page, pageSize);
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            return objectMapper.readValue(value.toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CommentDTO.class));
        } catch (Exception e) {
            log.error("[RedisRepository] getUserComments error : {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 修改删除缓存方法，同时删除评论数量缓存
     */
    @Override
    public void deleteCommentCache(String articleId) {
        if (articleId == null) {
            return;
        }
        try {
            // 删除评论数量缓存
            String countKey = String.format(RedisConstant.COMMENT_COUNT, articleId);
            redisTemplate.delete(countKey);

            // 删除评论列表缓存 (使用模式匹配)
            String topCommentsPattern = String.format(RedisConstant.ARTICLE_TOP_COMMENTS_PATTERN, articleId);
            String childCommentsPattern = String.format(RedisConstant.ARTICLE_CHILD_COMMENTS_PATTERN, articleId);

            Set<String> topKeys = redisTemplate.keys(topCommentsPattern);
            Set<String> childKeys = redisTemplate.keys(childCommentsPattern);

            if (!topKeys.isEmpty()) {
                redisTemplate.delete(topKeys);
            }

            if (!childKeys.isEmpty()) {
                redisTemplate.delete(childKeys);
            }
        } catch (Exception e) {
            log.error("[RedisRepository] deleteCommentCache error : {}", e.getMessage(), e);
        }
    }

    @Override
    public void deleteCommentChildCache(String articleId, String parentCommentId) {
        if (articleId == null || parentCommentId == null) {
            return;
        }
        try {
            // 删除子评论数量缓存
            String cacheKey = String.format(RedisConstant.COMMENT_COUNT,
                    String.format(RedisConstant.COMMENT_CHILD_COUNT, articleId, parentCommentId));
            redisTemplate.delete(cacheKey);

            // 删除子评论列表缓存
            String pattern = String.format(RedisConstant.ARTICLE_CHILD_COMMENTS_BY_PARENT_PATTERN, articleId, parentCommentId);
            Set<String> keys = redisTemplate.keys(pattern);
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.error("[RedisRepository] deleteCommentChildCache error : {}", e.getMessage(), e);
        }
    }

    @Override
    public void deleteChildCommentCountCache(String articleId, String commentId) {
        if (articleId == null || commentId == null) {
            return;
        }
        try {
            String cacheKey = String.format(RedisConstant.COMMENT_COUNT,
                    String.format(RedisConstant.COMMENT_CHILD_COUNT, articleId, commentId));
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.error("[RedisRepository] deleteChildCommentCountCache error : {}", e.getMessage(), e);
        }
    }

    @Override
    public void deleteAllChildCommentsCache(String articleId, String commentId) {
        if (articleId == null || commentId == null) {
            return;
        }
        try {
            // 删除所有相关的子评论缓存
            String pattern = String.format(RedisConstant.ARTICLE_CHILD_COMMENTS_BY_PARENT_PATTERN, articleId, commentId);
            Set<String> keys = redisTemplate.keys(pattern);
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }

            // 删除子评论计数缓存
            String cacheKey = String.format(RedisConstant.COMMENT_COUNT,
                    String.format(RedisConstant.COMMENT_CHILD_COUNT, articleId, commentId));
            redisTemplate.delete(cacheKey);

            log.info("[RedisRepository] deleteAllChildCommentsCache success for articleId: {}, commentId: {}", articleId, commentId);
        } catch (Exception e) {
            log.error("[RedisRepository] deleteAllChildCommentsCache error : {}", e.getMessage(), e);
        }
    }


    @Override
    public void deleteUserCommentCache(String userId) {
        if (userId == null) {
            return;
        }
        try {
            // 删除用户评论数量缓存
            String countKey = String.format(RedisConstant.COMMENT_COUNT, "user:" + userId);
            redisTemplate.delete(countKey);

            // 删除用户评论列表缓存
            String pattern = String.format(RedisConstant.USER_COMMENTS_PATTERN, userId);
            Set<String> keys = redisTemplate.keys(pattern);
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.error("[RedisRepository] deleteUserCommentCache error : {}", e.getMessage(), e);
        }
    }
}
