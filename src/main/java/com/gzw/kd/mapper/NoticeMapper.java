package com.gzw.kd.mapper;

import com.gzw.kd.common.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author gzw
 * @description：
 * @since：2023/5/20 17:24
 */
@Repository
@Mapper
public interface NoticeMapper {

    /**
     *  新增
     * @param notice 公告
     * @return int
     */

    Integer add(Notice notice);


    /**
     * 获取公告内容
     * @return 内容
     */
    String getContext();
}
