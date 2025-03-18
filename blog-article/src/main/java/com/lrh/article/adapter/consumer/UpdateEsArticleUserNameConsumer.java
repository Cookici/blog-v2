package com.lrh.article.adapter.consumer;

import com.lrh.article.application.cqe.article.UserUpdateMessage;
import com.lrh.article.domain.repository.ArticleOperateRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "${rocketmq.user-update.topic}",
        consumerGroup = "${rocketmq.user-update.consumer.group}"
)
public class UpdateEsArticleUserNameConsumer implements RocketMQListener<UserUpdateMessage> {
    private final ArticleOperateRepository articleRepository;
    private final ThreadPoolExecutor threadPoolExecutor;

    public UpdateEsArticleUserNameConsumer(ArticleOperateRepository articleRepository, ThreadPoolExecutor threadPoolExecutor) {
        this.articleRepository = articleRepository;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    public void onMessage(UserUpdateMessage message) {
        log.info("[UpdateEsArticleUserNameConsumer] 收到用户更新消息: userId={}, userName={}", message.getUserId(), message.getUserName());
        threadPoolExecutor.submit(() -> {
            try {
                // 更新ES中该用户的所有文章的用户名
                Integer updatedCount = articleRepository.updateArticleEsUserName(message.getUserId(), message.getUserName());
                log.info("[UpdateEsArticleUserNameConsumer] 成功更新用户[{}]的文章用户名，共更新{}篇", message.getUserId(), updatedCount);
            } catch (Exception e) {
                log.error("[UpdateEsArticleUserNameConsumer] 更新用户文章用户名失败: userId={}, userName={}, error={}",
                        message.getUserId(), message.getUserName(), e.getMessage(), e);
            }
        });
    }
}
