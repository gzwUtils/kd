package com.gzw.kd.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 异步任务类型枚举类
 *
 * @author gzw
 */
@AllArgsConstructor
@Getter
public enum AsyncTaskTypeEnum implements BaseEnum {

    /**
     * 异步任务类型枚举类
     */

    ALL_LOG_EXPORT(0, "全量导出"),
    ADD_LOG_EXPORT(1, "增量导出");

    private final Integer code;
    private final String description;


    public static AsyncTaskTypeEnum getEnumByCode(Integer code) {
        AsyncTaskTypeEnum[] values = AsyncTaskTypeEnum.values();
        for (AsyncTaskTypeEnum taskTypeEnum : values) {
            if (taskTypeEnum.getCode().equals(code)) {
                return taskTypeEnum;
            }
        }
        return null;
    }


}
