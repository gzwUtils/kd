package com.gzw.kd.vo.output;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class AsyncTaskVo extends AsyncTaskOutput{


    private Long id;

    /**
     * 任务参数，任意格式，任务实现类自行解析
     */
    private String params;

    /**
     * 下载状态, 0:未处理, 1:处理中, 2:处理完成，3:处理失败
     */
    private String status;

    /**
     * 处理失败原因
     */
    private String errorMsg;

    /**
     * 异步任务类型，0: 全量导出，1 条件导出
     */
    private String type;

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

}
