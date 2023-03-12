package com.gzw.kd.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description： RedisLockDefinitionHolder  任务队列保存参数
 * @since：2023/2/15 18:29
 */
@Data
@Accessors(chain = true)
public class RedisLockDefinitionHolder {

    /**
     * 业务唯一 key
     */
    private String businessKey;

    /**
     * 加锁时间 （秒 s）
     */
    private Long lockTime;

    /**
     * 上次更新时间 （ ms）
     */
    private Long lastModifyTime;

    /**
     * 保存当前线程
     */
    private  Thread currentThread;

    /**
     * 总共尝试次数
     */
    private int tryCount;

    /**
     * 当前尝试次数
     */

    private  int currentCount;

    /**
     * 更新的时间周期 （毫秒） 公式 = 加锁时间 （转毫秒） / 3
     */

    private Long modifyPeriod;

    public RedisLockDefinitionHolder(String businessKey, Long lockTime, Long lastModifyTime, Thread currentThread, int tryCount) {
        this.businessKey = businessKey;
        this.lockTime = lockTime;
        this.lastModifyTime = lastModifyTime;
        this.currentThread = currentThread;
        this.tryCount = tryCount;
        this.modifyPeriod = lockTime * 1000 / 3;
    }

}
