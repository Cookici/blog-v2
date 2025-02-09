package com.lrh.oss.dto.cqe;

import com.lrh.common.constant.BusinessConstant;
import com.lrh.common.exception.ValidException;
import com.lrh.oss.dto.req.TextSensingReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextSensingCmd {

    private String text;

    public TextSensingCmd(TextSensingReq cmd) {
        if (cmd == null || cmd.getText().length() <= 0) {
            throw new ValidException(String.format(BusinessConstant.VALID_ERROR, "校验失败"));
        }
        this.text = cmd.getText();

    }
}
