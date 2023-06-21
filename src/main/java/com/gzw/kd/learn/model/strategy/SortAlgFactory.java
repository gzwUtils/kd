package com.gzw.kd.learn.model.strategy;

import com.google.common.collect.Maps;
import com.gzw.kd.common.exception.Asserts;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * @author gzw
 * @description：
 * @since：2023/6/21 13:53
 */
public class SortAlgFactory {

    private static final Map<String, SortAlg> DATA = Maps.newHashMapWithExpectedSize(2);

    static {
        DATA.put("query",new QuerySort());
        DATA.put("ex", new ExternalSort());
    }

    public static SortAlg getSortAlg(String type) {
        if (StringUtils.isBlank(type)) {
            Asserts.fail("type should not is null");
        }
       return DATA.get(type);
    }
}
