package com.gzw.kd.listener.event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author 高志伟
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class MsgEvent {

    /**
     * 用户
     */
    private String userName;

    /**
     * 事件
     */
    private String event;

    /**
     * 在线时间
     */

    private String onLineTime;


    /**
     * 离线时间
     */

    private String offLineTime;


    /**
     * 状态
     */
    private Integer status;

    /**
     * openID
     */
    private String openId;

    /**
     * 数据
     */
    private Map<String, String> data;


    /**
     * id
     */
    private Integer id;


    /**
     * templateId
     */
    private Integer templateId;





}
