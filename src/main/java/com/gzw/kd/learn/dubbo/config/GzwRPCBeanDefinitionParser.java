//package com.gzw.kd.learn.dubbo.config;
//
//
//
//import javax.swing.text.html.parser.Element;
//import org.springframework.beans.factory.support.BeanDefinitionBuilder;
//import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
//import org.springframework.util.StringUtils;
//
///**
// * @author gzw
// * @description：
// * @since：2023/5/11 11:30
// */
//@SuppressWarnings("all")
//public class GzwRPCBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
//
//    protected Class getBeanClass(Element element) {
//        return ReferenceBean.class;
//    }
//    protected void doParse(Element element, BeanDefinitionBuilder bean) {
//        String interfaceClass = element.getAttribute("interface").toString();
//        if (StringUtils.hasText(interfaceClass)) {
//            bean.addPropertyValue("interfaceClass", interfaceClass);
//        }
//    }
//}
