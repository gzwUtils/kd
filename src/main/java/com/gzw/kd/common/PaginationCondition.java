package com.gzw.kd.common;

import java.io.Serializable;
import lombok.Data;

/**
 * @author gzw
 * @description： 分页
 * @since：2023/3/10 15:07
 */
@Data
public class PaginationCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页数
     */

    private int pageNum;


    /**
     * 每页条数
     */

    private int pageSize;
}
