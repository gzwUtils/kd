package com.gzw.kd.vo.input;

import com.gzw.kd.common.annotation.FieldValidator;
import lombok.Data;

/**
 * @author gzw
 * @description：
 * @since：2023/4/21 00:52
 */

@Data
public class ValidTest {

    @FieldValidator(regex = "^[\u4e00-\u9fa5]{1,9}$",message = "name param error")
    private String name;
}
