package com.gzw.kd.learn.aviator;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.gzw.kd.common.utils.AviatorUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gzw
 * @description： aviator
 * @since：2023/11/11 22:19
 */
public class AviatorLearn {

    public static void main(String[] args) {
        List<String> objects = new ArrayList<>();
        AviatorEvaluator.addFunction(new AviatorUtils());
        Object execute = AviatorEvaluator.execute("2 * (3+5)");
        System.out.println(execute);
        Expression expression = AviatorEvaluator.compile("2 * (3 + 5)");
        System.out.println(expression.execute());
        Expression compile = AviatorEvaluator.compile("aviatorRound('%.4f',1.2345678)");
        System.out.println(compile.execute());
        Expression compile1 = AviatorEvaluator.compile(String.valueOf(objects.size() == 0));
        System.out.println(compile1.execute());
    }
}
