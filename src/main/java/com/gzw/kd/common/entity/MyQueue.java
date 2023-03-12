package com.gzw.kd.common.entity;

import lombok.Data;

import java.util.List;

/**
 * @author 高志伟
 */
@Data
public class MyQueue {

    private Integer size;

    private List<Object> data;
}
