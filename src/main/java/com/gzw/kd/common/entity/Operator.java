package com.gzw.kd.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author 高志伟
 */
@Data
@Slf4j
@Accessors(chain = true)
@SuppressWarnings("all")
public class Operator extends User implements Serializable {

    public static final long serialVersionUID=1L;

    /**最后用户最后活动时间*/
    private long lastActiveTime;
    /**系统是否锁定，默认值false（未锁定），true（锁定）*/
    private boolean sysLock = false ;
    /**默认展示菜单*/
    private String defaultMenu;
    /**页面展示形式 0 展示全部页面含左侧菜单及头部，1 仅展示中间页面*/
    private int target;
    /**登录方式，0 普通登录。1 单点登录*/
    private String loginWay;
    /**提示消息*/
    private String hintMessage;


    public Operator(){}


    public Operator(String noticeBoard,String hintMessage,String loginWay,int target,long lastActiveTime){
        this.hintMessage = hintMessage;
        this.loginWay = loginWay;
        this.target = target;
        this.lastActiveTime = lastActiveTime;
    }
}
