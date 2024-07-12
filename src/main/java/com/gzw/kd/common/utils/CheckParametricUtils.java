package com.gzw.kd.common.utils;
import java.util.Collection;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 校验参数的工具
 *
 * @author gaozhiwei
 */
@SuppressWarnings("all")
public class CheckParametricUtils {

    public static void check(Object object) throws GlobalException {
        if (object == null) {
            throw new GlobalException(ResultCodeEnum.PARAM_ABSENT);
        }
        if (object instanceof String) {
            if (StringUtils.isEmpty(object.toString())) {
                throw new GlobalException(ResultCodeEnum.PARAM_ABSENT);
            }
        }
        if (object instanceof Collection) {
            Collection collection = (Collection) object;
            if (CollectionUtils.isEmpty(collection)) {
                throw new GlobalException(ResultCodeEnum.PARAM_ABSENT);
            }
        }
        if (object instanceof MultipartFile){
            MultipartFile file = (MultipartFile)object;
            if (file.isEmpty()){
                throw new GlobalException(ResultCodeEnum.PARAM_ABSENT);
            }
        }
    }

    public static String check(String name, String condition, String defaultValue){
        if (condition.contains(name)) {
            return defaultValue;
        }
        return name;
    }
}
