package com.gzw.kd.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.gzw.kd.canal.bo.CanalData;
import com.gzw.kd.common.exception.GlobalException;
import java.util.*;
import static com.gzw.kd.common.Constants.INT_ZERO;
import static com.gzw.kd.common.enums.ResultCodeEnum.ERR_CANAL_MESSAGE_PARSE;

/**
 * @author gaozhiwei
 */
@SuppressWarnings("all")
public class CanalMessageUtil {

    public static List<CanalData> parseCanalData(String instance, Message message) throws GlobalException {
        if (ObjectUtil.isNull(message)) {
            return null;
        }
        List<CanalEntry.Entry> entries = message.getEntries();
        List<CanalData> canalDataList = new ArrayList<>(entries.size());
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN
                    || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChange;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new GlobalException(ERR_CANAL_MESSAGE_PARSE.getCode(), "ERROR ## parser of event has an error , data:" + entry.toString(), e);
            }

            CanalEntry.EventType eventType = rowChange.getEventType();

            if (eventType != CanalEntry.EventType.INSERT
                    && eventType != CanalEntry.EventType.UPDATE
                    && eventType != CanalEntry.EventType.DELETE) {
                continue;
            }

            final CanalData canalData = new CanalData();
            canalData.setInstance(instance);
            canalData.setIsDdl(rowChange.getIsDdl());
            canalData.setDatabase(entry.getHeader().getSchemaName());
            canalData.setTable(entry.getHeader().getTableName());
            canalData.setType(CanalData.Type.valueOf(eventType.toString()));
            canalData.setExecuteTime(entry.getHeader().getExecuteTime());
            canalData.setIsDdl(rowChange.getIsDdl());
            canalData.setTimestamp(System.currentTimeMillis());
            canalData.setSql(rowChange.getSql());
            canalDataList.add(canalData);
            List<Map<String, Object>> data = new ArrayList<>();
            List<Map<String, Object>> old = new ArrayList<>();

            if (!rowChange.getIsDdl()) {
                Set<String> updateSet = new HashSet<>();
                canalData.setPkNames(new ArrayList<>());
                int i = INT_ZERO;
                for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {

                    Map<String, Object> row = new LinkedHashMap<>();
                    List<CanalEntry.Column> columns;

                    if (eventType == CanalEntry.EventType.DELETE) {
                        columns = rowData.getBeforeColumnsList();
                    } else {
                        columns = rowData.getAfterColumnsList();
                    }

                    for (CanalEntry.Column column : columns) {
                        if (i == 0) {
                            if (column.getIsKey()) {
                                canalData.getPkNames().add(column.getName());
                            }
                        }
                        if (column.getIsNull()) {
                            row.put(column.getName(), null);
                        } else {
                            row.put(column.getName(),
                                    JdbcTypeUtil.typeConvert(canalData.getTable(),
                                            column.getName(),
                                            column.getValue(),
                                            column.getSqlType(),
                                            column.getMysqlType()));
                        }
                        // 获取update为true的字段
                        if (column.getUpdated()) {
                            updateSet.add(column.getName());
                        }
                    }
                    if (!row.isEmpty()) {
                        data.add(row);
                    }

                    if (eventType == CanalEntry.EventType.UPDATE) {
                        Map<String, Object> rowOld = new LinkedHashMap<>();
                        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
                            if (updateSet.contains(column.getName())) {
                                if (column.getIsNull()) {
                                    rowOld.put(column.getName(), null);
                                } else {
                                    rowOld.put(column.getName(),
                                            JdbcTypeUtil.typeConvert(canalData.getTable(),
                                                    column.getName(),
                                                    column.getValue(),
                                                    column.getSqlType(),
                                                    column.getMysqlType()));
                                }
                            }
                        }
                        // update操作将记录修改前的值
                        if (!rowOld.isEmpty()) {
                            old.add(rowOld);
                        }
                    }

                    i++;
                }
                if (!data.isEmpty()) {
                    canalData.setData(data);
                }
                if (!old.isEmpty()) {
                    canalData.setOld(old);
                }
            }
        }

        return canalDataList;
    }
}
