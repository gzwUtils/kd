package com.gzw.kd.common.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
/**
 * @author gzw
 */
@Data
public class Doc {

    private int  id;

    /**
     * 流转状态
     */
    private Integer  status;

    /**
     * 派单人
     */
    private String  dispatch;

    /**
     * 指派人
     */

    private String appoint;

    /**
     * 发行日期
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING,timezone = "GMT+8")
    private LocalDateTime appointDate;

    /**
     * 指派日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING,timezone = "GMT+8")
    private LocalDateTime issueDate;

    /**
     * 描述
     */

    private String desc;

    /**
     * 审批人
     */

    private String audit;

    /**
     * 核稿人
     */

    private String verify;
    /**
     * 发送结果
     */

    private String result;

    /**
     * 备注
     */

    private String remark;

    /**
     * 客户住址
     */

    private String address;

    /**
     * 客户姓名
     */

    private String customerName;

    private String statusName;

    private String appointDates;

    private String tempSys;


    private Integer tempId;


}
