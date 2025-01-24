package com.lrh.message.dto.req;

import com.lrh.message.dto.MessageReqDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ProjectName: blog-v2
 * @Package: com.lrh.message.dto.req
 * @ClassName: MessageChangeStatusReq
 * @Author: 63283
 * @Description:
 * @Date: 2025/1/24 16:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageChangeStatusReq {

    private List<MessageReqDTO> messageReqDTOList;

}
