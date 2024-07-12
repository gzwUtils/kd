package com.gzw.kd.vo.input;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author gzw
 * @description：
 * @since：2023/5/10 16:32
 */
@SuppressWarnings("all")
@Data
public class StudentLeaveInput {

    @NotBlank(message = "process key is empty ")
    private String processKey;
    @NotBlank(message = "businessKey is empty ")
    private String businessKey;

    private String group;

    private Map map;
}
