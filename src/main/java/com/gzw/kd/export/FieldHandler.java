package com.gzw.kd.export;

import cn.hutool.poi.excel.StyleSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.function.Function;

/**
 * @author 高志伟
 */

@AllArgsConstructor
public class FieldHandler {

    @Getter
    private final String cellName;
    @Getter
    private final String dataFieldName;
    @Getter
    private final Function<Object, Object> handlerFunction;
    @Getter
    private final Function<BigExcelWriterNew, StyleSet> styleSetFunction;

    public FieldHandler(String cellName, String dataFieldName, Function<Object, Object> handlerFunction) {
        this.cellName = cellName;
        this.dataFieldName = dataFieldName;
        this.handlerFunction = handlerFunction;
        this.styleSetFunction = null;
    }
}
