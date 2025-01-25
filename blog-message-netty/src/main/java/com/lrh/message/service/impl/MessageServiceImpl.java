package com.lrh.message.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.context.UserContext;
import com.lrh.message.constants.MessageConstant;
import com.lrh.message.dto.MessageReqDTO;
import com.lrh.message.dto.PageDTO;
import com.lrh.message.dto.req.MessageChangeStatusReq;
import com.lrh.message.dto.req.MessageGetOfflineReq;
import com.lrh.message.dto.req.MessagePageReq;
import com.lrh.message.mapper.MessageMapper;
import com.lrh.message.model.MessageModel;
import com.lrh.message.netty.message.MessageVO;
import com.lrh.message.service.MessageService;
import com.lrh.message.utils.MessageUtil;
import com.lrh.message.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.message.service.impl
 * @ClassName: MessageServiceImpl
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/10 16:29
 */
@Slf4j
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, MessageModel> implements MessageService {


    private final MessageMapper messageMapper;

    private final RedisTemplate<String, Object> redisTemplate;


    public MessageServiceImpl(MessageMapper messageMapper, RedisTemplate<String, Object> redisTemplate) {
        this.messageMapper = messageMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public PageDTO<MessageVO> getMessagePage(MessagePageReq req) {
        if (!req.getUserId().equals(UserContext.getUserId())) {
            throw new RuntimeException("非法请求");
        }
        if (req.getPageNum() < 1) {
            req.setPageNum(1L);
        }
        if (req.getPageSize() < 1) {
            req.setPageSize(10L);
        }

        String redisKey = RedisKeyUtil.getMessageOneToOneRedisKey(req.getUserId(), req.getToUserId());
        long totalCache = getMessagePageCountCache(redisKey);
        if (totalCache == 0) {
            Long total = getMessagePageCount(req);
            if (total == null || total == 0L) {
                return new PageDTO<>();
            }
            List<MessageModel> messageModelList = getMessageModelPage(req);
            List<MessageVO> messageVOList = new ArrayList<>();
            messageModelList.forEach(messageModel -> {
                MessageVO messageVO = getMessageVO(messageModel);
                messageVOList.add(messageVO);
            });
            //TODO 设置缓存
            return PageDTO.<MessageVO>builder()
                    .data(messageVOList)
                    .total(total)
                    .page(req.getPageNum())
                    .pageSize(req.getPageSize())
                    .build();
        } else {
            Set<ZSetOperations.TypedTuple<Object>> messagePageCache =
                    getMessagePageCache(redisKey, req.getPageNum(), req.getPageSize());
            List<MessageVO> messageVOList = MessageUtil.redisMessageConvertToMessageVO(messagePageCache);
            return PageDTO.<MessageVO>builder()
                    .data(messageVOList)
                    .total(totalCache)
                    .page(req.getPageNum())
                    .pageSize(req.getPageSize())
                    .build();
        }
    }

    /**
     * ARGV[1]: ZSet 分值 (timestamp)
     * ARGV[2]: ZSet 数据 (序列化的 MessageModel)
     * ARGV[3]: 过期时间 (秒)
     *
     * @param messageVO 消息前端展示
     */
    @Override
    public void setCache(MessageVO messageVO) {
        String redisKey = RedisKeyUtil.getMessageOneToOneRedisKey(messageVO.getUserId(), messageVO.getToUserId());
        String luaScript =
                "redis.call('ZADD', KEYS[1], ARGV[1], ARGV[2]) " +
                        "redis.call('EXPIRE', KEYS[1], ARGV[3])";

        List<String> keys = Collections.singletonList(redisKey);
        List<Object> args = Arrays.asList(
                messageVO.getTimestamp(),
                messageVO,
                Duration.ofDays(7).getSeconds()
        );

        RedisScript<Void> redisScript = new DefaultRedisScript<>(luaScript, Void.class);
        redisTemplate.execute(redisScript, keys, args.toArray());
        log.info("[MessageServiceImpl] setCache {},redis缓存成功", messageVO);
    }


    @Override
    public MessageVO getLastMessage(String userId, String friendId) {
        if (!userId.equals(UserContext.getUserId())) {
            throw new RuntimeException("非法请求");
        }
        String redisKey = RedisKeyUtil.getMessageOneToOneRedisKey(userId, friendId);
        Set<ZSetOperations.TypedTuple<Object>> resultSet = redisTemplate.opsForZSet().reverseRangeWithScores(redisKey, 0, 0);
        if (resultSet != null && !resultSet.isEmpty()) {
            ZSetOperations.TypedTuple<Object> tuple = resultSet.iterator().next();
            List<MessageVO> messageVOList = MessageUtil.redisMessageConvertToMessageVO(new HashSet<>(Collections.singletonList(tuple)));
            return messageVOList.get(0);
        }

        LambdaQueryWrapper<MessageModel> queryWrapper = Wrappers.lambdaQuery(MessageModel.class)
                .and(wrapper -> wrapper
                        .eq(MessageModel::getUserId, userId)
                        .eq(MessageModel::getToUserId, friendId)
                        .or()
                        .eq(MessageModel::getUserId, friendId)
                        .eq(MessageModel::getToUserId, userId))
                .eq(MessageModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .orderByDesc(MessageModel::getTimestamp)
                .last("limit 1");
        MessageModel messageModel = messageMapper.selectOne(queryWrapper);
        return getMessageVO(messageModel);
    }

    @Override
    public void changeStatus(MessageChangeStatusReq req) {
        if (req == null || req.getMessageReqDTOList() == null || req.getMessageReqDTOList().isEmpty()) {
            return;
        }

        for (MessageReqDTO messageReqDTO : req.getMessageReqDTOList()) {
            if (messageReqDTO == null) {
                continue;
            }
            String redisKey = RedisKeyUtil.getMessageOneToOneRedisKey(messageReqDTO.getUserId(), messageReqDTO.getToUserId());
            Long timestamp = messageReqDTO.getTimestamp();
            Set<Object> messageVOSet = redisTemplate.opsForZSet().rangeByScore(redisKey, timestamp, timestamp);
            if (messageVOSet == null || messageVOSet.isEmpty()) {
                continue;
            }
            for (Object object : messageVOSet) {
                MessageVO messageVO = JSON.parseObject(JSON.toJSONString(object), MessageVO.class);
                if (messageVO != null && messageVO.getMessageId().equals(messageReqDTO.getMessageId())) {
                    messageVO.setMessageStatus("online");
                    redisTemplate.opsForZSet().remove(redisKey, object);
                    redisTemplate.opsForZSet().add(redisKey, messageVO, timestamp);
                    break;
                }
            }
        }

        MessageReqDTO messageReqDTO = req.getMessageReqDTOList().get(0);
        String userId = messageReqDTO.getUserId();
        String toUserId = messageReqDTO.getToUserId();
        List<String> messageIdList = req.getMessageReqDTOList().stream()
                .map(MessageReqDTO::getMessageId)
                .collect(Collectors.toList());
        LambdaUpdateWrapper<MessageModel> updateWrapper = Wrappers.lambdaUpdate(MessageModel.class)
                .eq(MessageModel::getUserId, userId)
                .eq(MessageModel::getToUserId, toUserId)
                .in(MessageModel::getMessageId, messageIdList)
                .eq(MessageModel::getMessageStatus, MessageConstant.STATUS_OFFLINE)
                .set(MessageModel::getMessageStatus, MessageConstant.STATUS_ONLINE);

        messageMapper.update(updateWrapper);
    }

    @Override
    public Map<String, Long> getOfflineMessageCount(MessageGetOfflineReq req) {
        if (!req.getUserId().equals(UserContext.getUserId())) {
            throw new RuntimeException("非法请求");
        }
        if (req.getUserIds() == null || req.getUserIds().isEmpty()) {
            throw new RuntimeException("非法请求");
        }
        LambdaQueryWrapper<MessageModel> queryWrapper = Wrappers.lambdaQuery(MessageModel.class)
                .in(MessageModel::getUserId, req.getUserIds())
                .eq(MessageModel::getToUserId, UserContext.getUserId())
                .eq(MessageModel::getMessageStatus, MessageConstant.STATUS_OFFLINE)
                .eq(MessageModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED);

        List<MessageModel> messageModelList = messageMapper.selectList(queryWrapper);
        Map<String, Long> result = new HashMap<>();
        for (MessageModel messageModel : messageModelList) {
            String userId = messageModel.getUserId();
            result.put(userId, result.getOrDefault(userId, 0L) + 1);
        }

        return result;
    }


    private List<MessageModel> getMessageModelPage(MessagePageReq req) {
        LambdaQueryWrapper<MessageModel> queryWrapper = Wrappers.lambdaQuery(MessageModel.class)
                .and(wrapper -> wrapper
                        .eq(MessageModel::getUserId, req.getUserId())
                        .eq(MessageModel::getToUserId, req.getToUserId())
                        .or()
                        .eq(MessageModel::getUserId, req.getToUserId())
                        .eq(MessageModel::getToUserId, req.getUserId()))
                .eq(MessageModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED)
                .orderByAsc(MessageModel::getTimestamp)
                .last("limit " + req.getOffset() + "," + req.getLimit());
        return messageMapper.selectList(queryWrapper);
    }

    private MessageVO getMessageVO(MessageModel messageModel) {
        if (messageModel == null) {
            return new MessageVO();
        }
        MessageVO messageVO = new MessageVO();
        messageVO.setMessageType(messageModel.getMessageType());
        messageVO.setMessageContent(messageModel.getMessageContent());
        messageVO.setToUserId(messageModel.getToUserId());
        messageVO.setUserId(messageModel.getUserId());
        messageVO.setTimestamp(messageModel.getTimestamp());
        messageVO.setMessageStatus(messageModel.getMessageStatus());
        return messageVO;
    }

    private Long getMessagePageCount(MessagePageReq req) {
        LambdaQueryWrapper<MessageModel> queryWrapper = Wrappers.lambdaQuery(MessageModel.class)
                .eq(MessageModel::getUserId, req.getUserId())
                .eq(MessageModel::getToUserId, req.getToUserId())
                .eq(MessageModel::getIsDeleted, BusinessConstant.IS_NOT_DELETED);
        return messageMapper.selectCount(queryWrapper);
    }


    /**
     * 分页查询 ZSet
     *
     * @param key      ZSet 的键
     * @param page     页码，从 1 开始
     * @param pageSize 每页的大小
     * @return 查询结果
     */
    public Set<ZSetOperations.TypedTuple<Object>> getMessagePageCache(String key, long page, long pageSize) {
        long start = (page - 1) * pageSize;
        long end = start + pageSize - 1;
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    /**
     * 获取 ZSet 总数
     *
     * @param key ZSet 的键
     * @return ZSet 的总数
     */
    public long getMessagePageCountCache(String key) {
        Long size = redisTemplate.opsForZSet().size(key);
        return size != null ? size : 0;
    }


}
