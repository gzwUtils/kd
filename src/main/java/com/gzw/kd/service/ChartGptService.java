package com.gzw.kd.service;

import java.io.IOException;

/**
 * @author gzw
 * @description：
 * @since：2023/4/4 16:17
 */
public interface ChartGptService {


    /**
     *  ai send
     * @param prompt 问题
     * @return
     */

    String send(String prompt) throws IOException;

}
