package com.gzw.kd.common.enums;

import lombok.Getter;

/**
 * @author gzw
 * @description： redis lock type
 * @since：2023/2/15 18:15
 */
@Getter
public enum RedisLockTypeEnum {


    /**
     * 自定义 key prefix
     */

    TEST("one","test1"),
    PRE("two","test2"),
    USER_LOCK("user_lock","user_lock");



    RedisLockTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final String code;

    private final String desc;


    public String getUniqueKey(String key){

        return String.format("%s:%s",this.getCode(),key);

    }
}
