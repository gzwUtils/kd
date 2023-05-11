//package com.gzw.kd.learn.dubbo.config;
//
//import com.gzw.kd.learn.dubbo.service.ProxyFactory;
//
///**
// * @author gzw
// * @description：
// * @since：2023/5/11 11:09
// */
//
//@SuppressWarnings("all")
//public class ReferenceConfig<T> {
//
//    /**
//     * 通配符  泛指所有类型
//     */
//    private Class<?> interfaceClass;
//
//    /**
//     * 私有的接口代理类引用 不进行序列化
//     */
//    private transient volatile  T ref;
//
//    public T getRef() {
//        if (ref == null) {
//            init();
//        }
//        return ref;
//    }
//
//    private void init() {
//        ref = new ProxyFactory(interfaceClass).getProxyObject();
//    }
//    public Class<?> getInterfaceClass() {
//        return interfaceClass;
//    }
//    public void setInterfaceClass(Class<?> interfaceClass) {
//        this.interfaceClass = interfaceClass;
//    }
//}
