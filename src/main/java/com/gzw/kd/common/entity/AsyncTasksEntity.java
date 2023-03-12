package com.gzw.kd.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 异步任务实体类
 *
 * @author gzw
 */
@Data
@Accessors(chain = true)
public class AsyncTasksEntity {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 任务参数，任意格式，任务实现类自行解析
     */
    private String params;

    /**
     * 下载状态, 0:未处理, 1:处理中, 2:处理完成，3:处理失败
     */
    private Integer status;

    /**
     * 处理失败原因
     */
    private String errorMsg;

    /**
     * 异步任务类型，0: 全量导出，1 条件导出
     */
    private Integer type;

    /**
     * 导出来源菜单
     */
    private String fromMenu;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private String updater;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 导出名称
     */
    private String exportName;


    /**
     * filePath
     */
    private String filePath;

    /**
     * 处理成功的条数
     */
    private Long numberOfSuccesses;

    /**
     * 处理失败的条数
     */
    private Long numberOfFailed;
}
