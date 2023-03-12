package com.gzw.kd.vo.input;

import com.gzw.kd.common.PaginationCondition;
import com.gzw.kd.common.annotation.EsSearchCondition;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description： log 查询 input
 * @since：2023/3/10 14:59
 */
@Accessors(chain = true)
@Data
public class LogSearchInput  extends PaginationCondition {

    /**
     * id range 查询 gte
     */
    @EsSearchCondition(fieldName = "id", isRangeQuery = true)
    private Long idGte;

    /**
     * id range 查询 lte
     */
    @EsSearchCondition(fieldName = "id", isRangeQuery = true)
    private Long idLte;

    /**
     * 入库查询开始时间
     */
    @EsSearchCondition(fieldName = "create_time", isRangeQuery = true)
    private String createTimeStart;

    /**
     * 入库查询结束时间
     */
    @EsSearchCondition(fieldName = "create_time", isRangeQuery = true)
    private String createsTimeEnd;


    /**
     * result 操作结果
     */
    @EsSearchCondition(fieldName = "result")
    private String result;


    /**
     * operation 操作
     */
    @EsSearchCondition(fieldName = "operation")
    private String operation;


    /**
     * location 地址
     */
    @EsSearchCondition(fieldName = "location")
    private String location;

    /**
     * ip ip
     */
    @EsSearchCondition(fieldName = "ip")
    private String ip;


    @EsSearchCondition(fieldName = "username")
    private String userName;


    //---------------------------- log导出 -----------------------------------------------------------------//

    /**
     * 导出字段
     */
    @NotEmpty(message = "导出字段未设置")
    private List<String> exportFields;

    /**
     * 数据存储枚举类的code值，用来区分请求来源
     */
    private Integer metaDataType;

    /**
     * 导出名称
     */
    private String exportFileName;

    /**
     * 导出来源菜单
     */
    private String fromMenu;
}
