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
                put("id", new FieldHandler("编号", "id", t -> t));
                put("username", new FieldHandler("姓名", "username", t -> t));
                put("ip", new FieldHandler("地址", "ip", t -> t));
                put("location", new FieldHandler("地点", "location", t -> t));
                put("operation", new FieldHandler("操作", "operation", t -> t));
                put("desc", new FieldHandler("描述", "desc", t -> t));
                put("result", new FieldHandler("结果", "result", t -> t));
                put("createTime", new FieldHandler("操作时间", "createTime", t -> t));
            }
        };
    }
}