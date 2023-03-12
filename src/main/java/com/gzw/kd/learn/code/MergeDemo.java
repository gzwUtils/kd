package com.gzw.kd.learn.code;

import java.util.Arrays;

/**
 * 项目名称：spring-demo
 * 类 名 称：MergeDemo
 * 类 描 述：merge 数组
 * 创建时间：2021/6/1 8:50 下午
 *
 * @author gzw
 */
public class MergeDemo {

    public static void main(String[] args) {
        int[] a = {1, 3, 5, 7, 9};
        int[] b = {2, 4, 6, 8};
        int[] k = new int[a.length + b.length];
        int i = a.length - 1;
        int j = b.length - 1;
        int z = k.length - 1;
        while (i >= 0) {
            k[z--] = j>=0 && a[i] < b[j] ?  b[j--] :a[i--] ;
        }
        Arrays.stream(k).forEach(System.out::println);

    }
}
