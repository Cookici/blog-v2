package com.lrh.article.infrastructure.database.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrh.article.application.dto.comment.CommentDailyCountDTO;
import com.lrh.article.infrastructure.po.CommentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ProjectName: blog-ddd
 * @Package: com.lrh.article.infrastructure.database.mapper
 * @ClassName: CommentMapper
 * @Author: 63283
 * @Description:
 * @Date: 2024/12/24 16:57
 */
@Mapper
public interface CommentMapper extends BaseMapper<CommentPO> {

    List<CommentPO> selectParentCommentPage(@Param("articleId") String articleId,
                                            @Param("offset") Long offset,
                                            @Param("limit") Long limit);

    List<CommentPO> selectChildCommentPage(@Param("articleId") String articleId,
                                           @Param("commentId") String commentId,
                                           @Param("offset") Long offset,
                                           @Param("limit") Long limit);

    List<CommentDailyCountDTO> selectCommentDailyCount(@Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);
}
