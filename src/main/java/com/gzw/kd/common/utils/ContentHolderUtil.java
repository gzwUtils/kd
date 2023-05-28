package com.gzw.kd.common.utils;

import java.text.MessageFormat;
import java.util.Map;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.PropertyPlaceholderHelper;

/**
 * @author gzw
 * @description： 内容占位符 替换
 * @since：2023/5/25 23:18
 */

@SuppressWarnings("all")
public class ContentHolderUtil {

    /**
     * 占位符前缀
     */
    private static final String PLACE_HOLDER_PREFIX = "${";

    /**
     * 占位符后缀
     */
    private static final String PLACE_HOLDER_SUFFIX = "}";

    private static final StandardEvaluationContext EVALUATION_CONTEXT;

    private static final PropertyPlaceholderHelper PROPERTY_PLACEHOLDER_HELPER = new PropertyPlaceholderHelper(
            PLACE_HOLDER_PREFIX, PLACE_HOLDER_SUFFIX);

    static {
        EVALUATION_CONTEXT = new StandardEvaluationContext();
        EVALUATION_CONTEXT.addPropertyAccessor(new MapAccessor());
    }

    public static String replacePlaceHolder(final String template, final Map<String, String> paramMap) {
        return PROPERTY_PLACEHOLDER_HELPER.replacePlaceholders(template,
                new CustomPlaceholderResolver(template, paramMap));
    }

    private static class CustomPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {
        private final String template;
        private final Map<String, String> paramMap;

        public CustomPlaceholderResolver(String template, Map<String, String> paramMap) {
            super();
            this.template = template;
            this.paramMap = paramMap;
        }

        @Override
        public String resolvePlaceholder(String placeholderName) {
            String value = paramMap.get(placeholderName);
            if (null == value) {
                String errorStr = MessageFormat.format("template:{0} require param:{1},but not exist! paramMap:{2}",
                        template, placeholderName, paramMap.toString());
                throw new IllegalArgumentException(errorStr);
            }
            return value;
        }
    }
}
