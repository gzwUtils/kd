package com.gzw.kd.learn.model.strategy;

import com.gzw.kd.common.exception.Asserts;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * @author gzw
 * @description：
 * @since：2023/6/21 14:18
 */
public class Sort {

    private static final long GB = 1024 * 1024 * 1024;

    private static final List<AlgRange> ALG = new ArrayList<>();

    static {
        ALG.add(new AlgRange(0,6 * GB,SortAlgFactory.getSortAlg("query")));
        ALG.add(new AlgRange(6,20 * GB,SortAlgFactory.getSortAlg("ex")));
    }

    public void sortFile(String filePath){
        if(StringUtils.isBlank(filePath)){
            Asserts.fail("file path should not is null");
        }
        File file = new File(filePath);
        long size = file.length();
        SortAlg sortAlg = null;
        for (AlgRange algRange : ALG) {
            if (algRange.inRange(size)) {
                sortAlg = algRange.getAlg();
                break;
            } }
        sortAlg.sort(filePath);
    }
}
