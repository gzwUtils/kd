package com.gzw.kd.canal.bo;

import cn.hutool.core.collection.CollUtil;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import static com.gzw.kd.common.Constants.INT_ZERO;

/**
 * 数据集对象
 * @author gaozhiwei
 */
@Data
public class CanalData implements Serializable {

    /** canal实例名 */
    private String instance;

    /** 数据库名 */
    private String database;

    /** 数据表名 */
    private String table;

    /** 删除标识 */
    private Boolean isDdl;

    /** 操作类型枚举 */
    private Enum<Type> type;

    /** 执行时间 */
    private Long executeTime;

    /** 时间戳 */
    private Long timestamp;

    /** 执行SQL语句 */
    private String sql;

    /** 主键名列表 */
    private List<String> pkNames;

    /** 变更后的字段及数据集 */
    private List<Map<String, Object>> data;

    /** 变更前的字段及数据集 */
    private List<Map<String, Object>> old;

    /**
     * 获取是为空数据集
     * @return 空数据集：true，非空数据集：false
     */
    public boolean isEmpty() {
        return CollUtil.isEmpty(data);
    }

    /**
     * 获取变更后的数据集大小
     * @return 数据集大小
     */
    public int size() {
        return Optional.ofNullable(data).filter(CollUtil::isNotEmpty).map(List::size).orElse(INT_ZERO);
    }

    /** 清空canal数据集 */
    public void clear() {
        database = null;
        table = null;
        type = null;
        executeTime = null;
        timestamp = null;
        data = null;
        old = null;
        sql = null;
    }

    /** 数据操作类型 */
    public enum Type {

        /**
         * 新增 修改 删除
         */
        INSERT, UPDATE, DELETE
    }
}
