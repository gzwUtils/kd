package com.gzw.kd.common;

/**
 * @author gzw
 * @description：
 * @since：2023/6/5 16:49
 */
@SuppressWarnings("all")
public interface XxlJobConstant {



    /**
     * 任务信息接口路径
     */
    public static final String LOGIN_URL = "/login";

    public static final String INSERT_URL = "/jobinfo/add";


    public static final String UPDATE_URL = "/jobinfo/update";

    public static final String DELETE_URL = "/jobinfo/remove";

    public static final String RUN_URL = "/jobinfo/start";

    public static final String STOP_URL = "/jobinfo/stop";

    /**
     * 执行器组接口路径
     */
    public static final String JOB_GROUP_PAGE_LIST = "/jobgroup/pageList";

    public static final String JOB_GROUP_INSERT_URL = "/jobgroup/save";

    /**
     * 超时时间
     */
    public static final Integer TIME_OUT = 120;

    /**
     * 失败重试次数
     */
    public static final Integer RETRY_COUNT = 0;

    /**
     * 立即执行的任务 延迟时间(秒数)
     */
    public static final Integer DELAY_TIME = 10;

    /**
     * cron时间格式
     */
    public final static String CRON_FORMAT = "ss mm HH dd MM ? yyyy";


    /**
     * 执行任务标题——
     */
    public static final String JOB_TITLE = "kd-test";


    /**
     * 重置后默认handler
     */
    public static final String EXECUTE_HANDLER_NAME = "";

    /**
     * 重置后默认描述
     */
    public static final String DESC = "重置模版中........";
}
