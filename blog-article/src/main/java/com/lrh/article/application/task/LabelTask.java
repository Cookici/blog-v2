package com.lrh.article.application.task;

import com.lrh.article.constants.RedisConstant;
import com.lrh.article.domain.repository.ArticleLabelOperateRepository;
import com.lrh.article.domain.repository.LabelOperateRepository;
import com.lrh.article.infrastructure.po.LabelPO;
import com.lrh.article.util.LockUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LabelTask {

    private final LabelOperateRepository labelOperateRepository;
    private final ArticleLabelOperateRepository articleLabelOperateRepository;
    private final RedissonClient redissonClient;

    public LabelTask(LabelOperateRepository labelOperateRepository, ArticleLabelOperateRepository articleLabelOperateRepository, RedissonClient redissonClient) {
        this.labelOperateRepository = labelOperateRepository;
        this.articleLabelOperateRepository = articleLabelOperateRepository;
        this.redissonClient = redissonClient;
    }

    /**
     * 每天凌晨0点执行
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void labelTask() {
        LockUtil lockUtil = new LockUtil(redissonClient);
        try {
            lockUtil.tryLock(RedisConstant.LABEL_LOCK,()->{
                List<LabelPO> labelPOList = labelOperateRepository.getDeletedLable();
                List<String> labelIdList = labelPOList.stream().map(LabelPO::getLabelId).collect(Collectors.toList());
                articleLabelOperateRepository.deleteArticleLabel(labelIdList);
            });
        } catch (Exception e) {
            log.error("同步删除label一致性失败",e);
        }
    }

}
