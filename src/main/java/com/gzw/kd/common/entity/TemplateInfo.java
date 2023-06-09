package com.gzw.kd.common.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description：
 * @since：2023/5/24 14:42
 */
@Accessors(chain = true)
@Data
public class TemplateInfo implements Serializable {


    /**
     * 模版id
     */
    private Long id;


    /**
     * 模板标题
     */
    private String name;


    /**
     * 消息状态
     */
    private Integer msgStatus;

    /**
     * 定时任务Id(由xxl-job返回)
     */
    private Integer cronTaskId;
    /**
     * 定时发送的人群的文件路径
     */
    private String cronCrowdPath;

    /**
     * 发送的Id类型
     */
    private Integer idType;

    /**
     * 发送渠道
     */
    private Integer sendChannel;

    /**
     * 消息内容  {$var} 为占位符
     */
    private String msgContent;

    /**
     * 模板类型
     */
    private Integer templateType;

    /**
     * 屏蔽类型
     */
    private Integer shieldType;

    /**
     * 消息类型
     */
    private Integer msgType;

    /**
     * 推送消息的时间
     * 0：立即发送
     * else：crontab 表达式
     */
    private String expectPushTime;

    /**
     * 发送账号
     */

    private String sendAccount;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 修改者
     */
    private String updator;


    /**
     * 是否删除
     * 0：未删除
     * 1：已删除
     */
    private Integer isDeleted;


    /**
     * 创建时间 单位 s
     */
    private Integer created;

    /**
     * 更新时间 单位s
     */
    private Integer updated;


}
