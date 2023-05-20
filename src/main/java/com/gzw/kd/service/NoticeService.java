package com.gzw.kd.service;

import com.gzw.kd.common.entity.Notice;

/**
 * @author gzw
 * @description：
 * @since：2023/4/4 16:17
 */
public interface NoticeService {


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
