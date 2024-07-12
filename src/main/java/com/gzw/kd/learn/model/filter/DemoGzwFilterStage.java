package com.gzw.kd.learn.model.filter;

import com.gzw.kd.common.entity.Operator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @since 2020/9/25
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class DemoGzwFilterStage {

    /** ----------------------------入参---------------------------------*/
    Operator operator;

    /** ----------------------------处理结果---------------------------------*/

    List<Map<String, Object>> stageHandlerResult = new ArrayList<>();

}