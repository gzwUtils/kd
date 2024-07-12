package com.gzw.kd.learn.model.filter;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author gzw
 * @since 2020/9/25
 */
@Slf4j
@SuppressWarnings("all")
@Component
public class DemoOneFilter implements GzwFilter<DemoGzwFilterStage> {



    @Override
    public DemoGzwFilterStage doFilter(DemoGzwFilterStage lastFilterStage) {
        log.info("DemoOneFilter--------------------------------------------------");
        List<Map<String, Object>> result = lastFilterStage.getStageHandlerResult();
        /**
         * 1、实际该步骤需要处理的逻辑。并返回
         */
        Map<String, Object> map = new HashMap<>(8);
        map.put("account-1",lastFilterStage.operator.getAccount());
        result.add(map);
        return lastFilterStage.setStageHandlerResult(result);
    }
}

