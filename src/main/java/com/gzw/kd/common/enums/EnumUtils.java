package com.gzw.kd.common.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author gzw
 * @description： 枚举工具类
 * @since：2023/7/15 10:42
 */
@SuppressWarnings("unused")
public class EnumUtils {

    private EnumUtils() {
    }



    public static <T extends BaseEnum> String getDescriptionByCode(Integer code, Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> Objects.equals(e.getCode(), code))
                .findFirst().map(BaseEnum::getDescription).orElse("");
    }

    public static <T extends BaseEnum> T getEnumByCode(Integer code, Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> Objects.equals(e.getCode(), code))
                .findFirst().orElse(null);
    }

    public static <T extends BaseEnum> List<Integer> getCodeList(Class<T> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(BaseEnum::getCode)
                .collect(Collectors.toList());
    }
}
