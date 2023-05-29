package com.gzw.kd.send;

import java.util.List;

/**
 * @author gzw
 * @description： 责任链执行结果 串起整个责任链
 * @since：2023/5/25 23:33
 */

@SuppressWarnings("all")
public class ProcessTemplate {

    private List<BusinessProcess> processList;

    public List<BusinessProcess> getProcessList() {
        return processList;
    }

    public void setProcessList(List<BusinessProcess> processList) {
        this.processList = processList;
    }
}
