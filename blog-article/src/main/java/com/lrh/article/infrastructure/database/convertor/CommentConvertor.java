package com.lrh.article.infrastructure.database.convertor;

import com.lrh.article.domain.entity.CommentEntity;
import com.lrh.article.infrastructure.po.CommentPO;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.database.convertor
 * @ClassName: CommentConvertor
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/24 01:48
 */

public class CommentConvertor {
    public static List<CommentEntity> toCommentEntityListConvertor(List<CommentPO> commentPOList) {
        List<CommentEntity> commentEntityList = new ArrayList<>();
        commentPOList.forEach(commentPO -> {
            commentEntityList.add(CommentEntity.fromPO(commentPO));
        });
        return commentEntityList;
    }
}
