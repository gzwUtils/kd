package com.gzw.kd.export.handler;

import com.gzw.kd.export.ExportFieldHandlerRegistry;
import com.gzw.kd.export.FieldHandler;
import com.gzw.kd.export.HandlerInit;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 高志伟
 */

@Component
public class TestSyncTaskExportHandler extends HandlerInit {

    private static final Map<String, FieldHandler> FIELD_HANDLERS;

    static {
        FIELD_HANDLERS = new LinkedHashMap<>();
        FIELD_HANDLERS.put("id", new FieldHandler("编号", "id", t -> t));
        FIELD_HANDLERS.put("username", new FieldHandler("姓名", "username", t -> t));
        FIELD_HANDLERS.put("ip", new FieldHandler("地址", "ip", t -> t));
        FIELD_HANDLERS.put("location", new FieldHandler("地点", "location", t -> t));
        FIELD_HANDLERS.put("operation", new FieldHandler("操作", "operation", t -> t));
        FIELD_HANDLERS.put("desc", new FieldHandler("描述", "desc", t -> t));
        FIELD_HANDLERS.put("result", new FieldHandler("结果", "result", t -> t));
        FIELD_HANDLERS.put("createTime", new FieldHandler("操作时间", "createTime", t -> t));
    }

    public TestSyncTaskExportHandler(ExportFieldHandlerRegistry exportFieldHandlerRegistry) {
        super(exportFieldHandlerRegistry.getTest());
    }

    @Override
    protected Map<String, FieldHandler> initialize() {
        return new LinkedHashMap<>(FIELD_HANDLERS);
    }
}