package com.gzw.kd.common.generators;


import com.gzw.kd.common.exception.GlobalException;

/**
 * @author 高志伟
 */
public interface IdGenerator {

    /**
     *  id generate
     * @param length
     * @return
     * @throws GlobalException
     */
    String generate(Integer length) throws GlobalException;

}
