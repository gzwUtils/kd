package com.gzw.kd.vo.input;
import java.util.Map;
import lombok.Data;

/**
 * @author gzw
 * @description：
 * @since：2023/5/10 16:32
 */
@SuppressWarnings("all")
@Data
public class StudentLeaveInput {

    private String processKey;

    private String businessKey;

    private String group;

    private Map map;
}
