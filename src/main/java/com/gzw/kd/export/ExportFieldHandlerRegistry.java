package com.gzw.kd.export;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 文件导出字段注册表
 * @author 高志伟
 */

@Data
@Component
public class ExportFieldHandlerRegistry {

    private final Map<String, FieldHandler> test = new LinkedHashMap<>();
}
