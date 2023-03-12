package com.gzw.kd.common.enums;

import lombok.Getter;

/**
 * 异步任务类型枚举类
 * @author ligq
 * @since 2020-05-29
 */
@Getter
public enum AsyncTaskStatusEnum{

    /**
     * 异步任务类型枚举类
     */

    UNTREATED(0, "未处理"),
    PROCESSING(1, "处理中"),
    SUCCESS(2, "处理完成"),
    FAILURE(3, "处理失败");

    private final int code;
    private final String desc;

    AsyncTaskStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
