package com.gzw.kd.vo.output;
import com.gzw.kd.common.entity.TemplateInfo;
import lombok.Data;


@Data
public class TemplateVo extends TemplateInfo {


    private String content;

    private String status;


    private String type;

    private String account;


    private  String createTime;
}
