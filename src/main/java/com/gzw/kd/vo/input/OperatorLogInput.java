package com.gzw.kd.vo.input;

import com.gzw.kd.common.entity.Log;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description： where log input
 * @since：2023/3/2 21:06
 */
@Accessors(chain = true)
@Data
public class OperatorLogInput extends Log {

    private  int size  ;
}
