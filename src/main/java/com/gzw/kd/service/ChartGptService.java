package com.gzw.kd.service;
/**
 * @author gzw
 * @description：
 * @since：2023/4/4 16:17
 */
public interface ChartGptService {


    /**
     *  gpt ai
     * @param prompt 问题
     * @return answer
     * @throws Exception err
     */

    String send(String prompt) throws Exception;

}
