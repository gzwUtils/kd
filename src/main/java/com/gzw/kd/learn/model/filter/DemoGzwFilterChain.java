package com.gzw.kd.learn.model.filter;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 执行
 * @author gzw
 * @since 2020/9/27
 */
@Component
public class DemoGzwFilterChain extends AbstractGzwFilterChain<DemoGzwFilterStage> {

    @Autowired
    public DemoGzwFilterChain(
            DemoOneFilter oneFilter,
            DemoTwoFilter twoFilter) {
        super(
                Arrays.asList(
                        twoFilter,
                        oneFilter));
    }
}
