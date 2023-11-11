package com.gzw.kd.common.utils;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： round 4
 * @since：2023/11/11 22:41
 */
@Component
@SuppressWarnings("all")
public class AviatorUtils extends AbstractFunction {

    private static final String BL4 = "%.4f" ;

    private static final String ROUND_NAME = "aviatorRound" ;

    @Override
    public String getName() {
        return ROUND_NAME;
    }

    /**
     * 保留 4位
     * @param env param
     * @param arg1 保留小数位
     * @param arg2 param
     * @return result
     */
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        String num1 = FunctionUtils.getStringValue(arg1, env);
        Number num2 = FunctionUtils.getNumberValue(arg2, env);
        num1 = num1 == null ? BL4 :num1;
        String result = String.format(num1, num2);
        return new AviatorString(result);
    }
}
