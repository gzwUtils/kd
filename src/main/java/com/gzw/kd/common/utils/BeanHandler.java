package com.gzw.kd.common.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Objects;

/**
 * @author gzw
 * @description： 利用反射讲数据转化为Object
 * @since：2023/4/25 20:34
 */
@SuppressWarnings("all")
public class BeanHandler<T> {

    private final Class<T> tClass;

    public BeanHandler(Class<T> tClass) {
        this.tClass = tClass;
    }

    /**
     * 讲sql 查询结果 ResultSet转化为对象
     * @param rs rs
     * @return T
     */

    public T handle(ResultSet rs) throws Exception {
        //结果集默认指向为第一个数据的前一个
        if (rs.next()) {
            //根据传入的字节码创建传入的指定对象
            T instance = tClass.newInstance();
            //获取指定字节码信息
            BeanInfo beanInfo = Introspector.getBeanInfo(tClass, Object.class);
            //获取所有属性描述器
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                //获取结果集中对应字段名的值
                Object o = rs.getObject(pd.getName());
                //执行当前方法并传入参数
                pd.getWriteMethod().invoke(instance, o);
            }
            return instance;
        }
        return null;
    }


    /**
     * 将map 利用反射转化为对象
     *
     * @param map map
     * @return T
     * @throws Exception sql
     */
    public T handle(Map<String, Object> map) throws Exception {
        //结果集默认指向为第一个数据的前一个
        //根据传入的字节码创建传入的指定对象
        T obj = tClass.newInstance();
        BeanInfo beanInfo = Introspector.getBeanInfo(tClass, Object.class);
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

        for (PropertyDescriptor pd : pds) {
            Object o = map.get(pd.getName());
            if (Objects.nonNull(o)) {
                // ！！！这里需要对属性类型做强制类型转化，
                o = getPropertyTypeObject(pd, o);
                // 下面的方法相当于属性的 set方法
                pd.getWriteMethod().invoke(obj, o);
            }
        }
        return obj;
    }



    /**
     * 将对应的mapValue 强转为实体类对应的类型
     *
     * @param pd pd
     * @param o o
     * @return o
     */
    public Object getPropertyTypeObject(PropertyDescriptor pd, Object o) {
        //当前属性的类型
        String name = pd.getPropertyType().getName();
        name = name.substring(name.lastIndexOf(".") + 1);
        switch (name) {
            case "String":
                o = String.valueOf(o);
                break;
            case "Long":
                o = Long.valueOf(String.valueOf(o));
                break;
            case "Double":
                o = Double.valueOf(String.valueOf(o));
                break;
            case "Integer":
                o = Integer.valueOf(String.valueOf(o));
                break;
            case "BigDecimal":
                o = new BigDecimal(String.valueOf(o));
                break;
            default:
                System.out.println("name:"+name);
        }
        return o;
    }
}
