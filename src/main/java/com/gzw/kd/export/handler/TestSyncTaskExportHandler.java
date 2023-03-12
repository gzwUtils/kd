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

    public TestSyncTaskExportHandler(ExportFieldHandlerRegistry exportFieldHandlerRegistry) {
        super(exportFieldHandlerRegistry.getTest());
    }

    @Override
    protected Map<String, FieldHandler> initialize() {
        return new LinkedHashMap<String, FieldHandler>() {
            {
                put("id", new FieldHandler("id", "id", t -> t));
                put("ip", new FieldHandler("ip", "ip", t -> t));
                put("username", new FieldHandler("姓名", "username", t -> t));
                put("operation", new FieldHandler("操作类型", "operation", t -> t));
                put("createTime", new FieldHandler("操作时间", "createTime", t -> t));
                put("result", new FieldHandler("结果", "result", t -> t));
            }
        };
    }
}