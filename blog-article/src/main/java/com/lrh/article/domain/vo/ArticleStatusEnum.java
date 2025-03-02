package com.lrh.article.domain.vo;

import lombok.Getter;

@Getter
public enum ArticleStatusEnum {
    UnderAudit("under_audit", "审核中"),

    FailedAudit("failed_audit", "审核不通过"),

    Published("published", "已发布"),

    Deleted("deleted", "已删除");

    private final String status;

    private final String description;

    ArticleStatusEnum(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public static ArticleStatusEnum getByStatus(String status) {
        for (ArticleStatusEnum articleStatusEnum : ArticleStatusEnum.values()) {
            if (articleStatusEnum.getStatus().equals(status)) {
                return articleStatusEnum;
            }
        }
        return null;
    }
}
