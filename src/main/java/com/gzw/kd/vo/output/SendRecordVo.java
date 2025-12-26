package com.gzw.kd.vo.output;

import lombok.Data;

import java.util.Date;


@Data
public class SendRecordVo {

    private Long id;

    private Long templateId;

    private String receiver;

    private Date time;

    private String status;
}
