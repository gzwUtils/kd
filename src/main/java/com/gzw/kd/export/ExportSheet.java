package com.gzw.kd.export;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.StyleSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 文件导出sheet设置
 * @author gzw
 */
@SuppressWarnings("all")
@AllArgsConstructor
public class ExportSheet {

    /**
     * 默认遍历batch size
     */
    private static final int DEFAULT_ITERATE_BATCH_SIZE = 50;

    /**
     * 默认sheet名称
     */
    private static final String DEFAULT_SHEET_NAME = "sheet1";

    @Getter
    private final String sheetName;
    @Getter
    private final Iterable<List<Map<String, Object>>> dataList;
    @Getter
    private final Map<String, FieldHandler> fieldHandler;
    @Getter
    private final String[] exportFields;
    @Getter
    private Iterable<StyleSet> styleSets;

    private final int maxBatchSize;

    public ExportSheet(Iterable<List<Map<String, Object>>> dataList, Map<String, FieldHandler> fieldHandler) {
        this(dataList, fieldHandler, DEFAULT_ITERATE_BATCH_SIZE);
    }

    public ExportSheet(Iterable<List<Map<String, Object>>> dataList, Map<String, FieldHandler> fieldHandler, String[] exportFields) {
        this(DEFAULT_SHEET_NAME, dataList, fieldHandler, exportFields, null, DEFAULT_ITERATE_BATCH_SIZE);
    }

    public ExportSheet(Iterable<List<Map<String, Object>>> dataList, Map<String, FieldHandler> fieldHandler, String[] exportFields, int maxBatchSize) {
        this(DEFAULT_SHEET_NAME, dataList, fieldHandler, exportFields, null, maxBatchSize);
    }

    public ExportSheet(Iterable<List<Map<String, Object>>> dataList, Map<String, FieldHandler> fieldHandler, int maxBatchSize) {
        this(DEFAULT_SHEET_NAME, dataList, fieldHandler, maxBatchSize);
    }

    public ExportSheet(String sheetName, Iterable<List<Map<String, Object>>> dataList, Map<String, FieldHandler> fieldHandler, int maxBatchSize) {
        this(StrUtil.isBlank(sheetName) ? DEFAULT_SHEET_NAME : sheetName,
                dataList, fieldHandler,
                fieldHandler.keySet().toArray(new String[0]),
                null,
                maxBatchSize);
    }

    /**
     * 滚动写入文件，不写表头
     */
    public void write2FileWithoutHeader(BigExcelWriterNew writer) {
        write2FileInternal(writer, false);
    }

    /**
     * 滚动写入文件
     */
    public void write2File(BigExcelWriterNew writer) {
        write2FileInternal(writer, true);
    }

    private void write2FileInternal(BigExcelWriterNew writer, boolean withHeader) {
        if (Objects.isNull(writer)) {
            return;
        }

        // 单元格样式
        this.styleSets = fieldHandler.values()
                .stream()
                .map(FieldHandler::getStyleSetFunction)
                .filter(Objects::nonNull)
                .map(e -> e.apply(writer))
                .collect(Collectors.toList());

        // 写标题
        if (withHeader) {
            if (CollUtil.isEmpty(this.styleSets)) {
                writer.writeHeadRow(getSheetHeaderSingle());
            } else {
                writer.writeHeadRow(getSheetHeaderSingle(), this.styleSets);
            }
        }

        final Map<String, FieldHandler> intersectHandlers
                = MapUtil.getAny(fieldHandler, exportFields);

        int batch = maxBatchSize;
        Iterator<List<Map<String, Object>>> itr = dataList.iterator();

        // 写数据, dataList中为一批数据, key为字段名称(需与handler中配置的field字段一致)
        while (itr.hasNext() && batch-- > 0) {
            List<Map<String, Object>> t = itr.next();
            // 按照handlers的顺序, 处理字段
            Collection<? extends List<Object>> afterHandlerList =
                    t.stream().map(m -> {
                        List<Object> fieldList = new ArrayList<>();
                        intersectHandlers.forEach((k, v) ->
                                fieldList.add(v.getHandlerFunction().apply(dataValue(v.getDataFieldName(), m))));
                        return fieldList;
                    }).filter(CollectionUtil::isNotEmpty).collect(Collectors.toList());
            if (!afterHandlerList.isEmpty()) {
                if (CollUtil.isEmpty(this.styleSets)) {
                    writer.write(afterHandlerList);
                } else {
                    writer.write(afterHandlerList, this.styleSets);
                }
            }
        }
    }

    private Object dataValue(final String originKey, final Map<String, Object> dataMap) {
        Object value = null;

        // 先尝试使用原始key获取值
        if (null == (value = dataMap.get(originKey))) {
            // 尝试使用驼峰命名方式获取值
            value = dataMap.get(StrUtil.toCamelCase(originKey));
        }

        // 尝试使用下划线分割的key获取值
        if (null == value) {
            value = dataMap.get(StrUtil.toSymbolCase(originKey, '_'));
        }

        return value;
    }

    private List<List<String>> getSheetHeader() {
        return Collections.singletonList(
                fieldHandlerMap2CellName(MapUtil.getAny(fieldHandler, exportFields).values()));
    }

    private List<String> getSheetHeaderSingle() {
        return fieldHandlerMap2CellName(MapUtil.getAny(fieldHandler, exportFields).values());
    }

    private List<String> fieldHandlerMap2CellName(Collection<FieldHandler> handlers) {
        return handlers.stream().map(FieldHandler::getCellName).distinct().collect(Collectors.toList());
    }
}
