package com.gzw.kd.send.service;

import com.gzw.kd.common.R;
import com.gzw.kd.vo.input.BatchSendInput;
import com.gzw.kd.vo.input.SendInput;

/**
 * @author gzw
 * @description： 发送
 * @since：2023/5/25 23:58
 */
public interface SendService {

    /**
     * 单文案发送接口
     *
     * @param sendInput input
     * @return result
     */
    R send(SendInput sendInput);


    /**
     * 多文案发送接口
     *
     * @param batchSendInput input
     * @return result
     */
    R batchSend(BatchSendInput batchSendInput);
}
