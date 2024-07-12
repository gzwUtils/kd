package com.gzw.kd.learn.model.strategy;

import lombok.Data;

/**
 * @author gzw
 * @description：
 * @since：2023/6/21 13:57
 */
@Data
public class AlgRange {

    private long start;
    private long end;
    private SortAlg alg;

    public AlgRange(long start, long end, SortAlg alg) {
        this.start = start;
        this.end = end;
        this.alg = alg;
    }

    public boolean inRange(long size){
        return size >= start && size < end;
    }

}
