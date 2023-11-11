package com.gzw.kd.learn.aviator;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.gzw.kd.common.utils.AviatorUtils;

/**
 * @author gzw
 * @description： aviator
 * @since：2023/11/11 22:19
 */
public class AviatorLearn {

    public static void main(String[] args) {
        AviatorEvaluator.addFunction(new AviatorUtils());
        Object execute = AviatorEvaluator.execute("2 * (3+5)");
        System.out.println(execute);
        Expression expression = AviatorEvaluator.compile("2 * (3 + 5)");
        System.out.println(expression.execute());
        Expression compile = AviatorEvaluator.compile("aviatorRound('%.4f',1.2345678)");
        System.out.println(compile.execute());
    }
}
