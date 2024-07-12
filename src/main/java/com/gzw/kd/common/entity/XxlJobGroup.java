package com.gzw.kd.common.entity;

import java.util.*;
import lombok.Builder;
import lombok.Data;

/**
 * @author gzw
 * @description： 执行器组
 * @since：2023/6/5 15:38
 */
@Builder
@Data
public class XxlJobGroup {

    private int id;

    private String appName;

    private String title;


    /**
     * 执行器地址类型：0=自动注册、1=手动录入
     */
    private int addressType;

    /**
     * 执行器地址列表，多地址逗号分隔(手动录入)
     */
    private String addressList;

    private Date updateTime;

    /**
     * registry list 执行器地址列表(系统注册)
     */
    private List<String> registryList;

    public List<String> getRegistryList() {
        if (Objects.nonNull(addressList) && addressList.trim().length() > 0) {
            registryList = new ArrayList<String>(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }

}
