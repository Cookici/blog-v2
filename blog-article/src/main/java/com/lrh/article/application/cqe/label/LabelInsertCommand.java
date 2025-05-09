package com.lrh.article.application.cqe.label;

import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LabelInsertCommand {

    private String labelName;

    private String labelAlias;

    private String labelDescription;

    public void valid() {
        if (this.labelName == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (this.labelName.isEmpty()) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (this.labelAlias == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (this.labelAlias.isEmpty()) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }

        if (this.labelDescription == null) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        if (this.labelDescription.isEmpty()) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
    }
}
