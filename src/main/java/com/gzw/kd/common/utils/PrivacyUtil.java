package com.gzw.kd.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author gzw
 * @description：
 * @since：2023/2/13 10:12
 */

@SuppressWarnings("all")
public class PrivacyUtil {


    /**
     * 隐藏中间四位
     * @param phone 手机号
     * @return 手机号
     */

    public static String hidePhone(String phone){
       return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
    }

    /**
     * 隐藏邮箱
     * @param email 邮箱
     * @return 邮箱
     */
    public static String hideEmail(String email){
        return email.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)","$1****$3$4");
    }

    /**
     * 隐藏身份证
     * @param idCard 身份证号
     * @return 身份证号
     */

    public static String hideIDCard(String idCard){
        return idCard.replaceAll("(\\d{4})\\d{10}(\\d{4})","$1****$2");
    }


    /**
     * 隐藏姓名
     * @param name 姓名
     * @param type 隐藏类型  默认*
     * @return 姓名
     */
    public static String hideName(String name,String type){
        if(StringUtils.isBlank(name)){
            return null;
        }
        type = type ==null?"*":type;
        return desValue(name,1,0,type);
    }


    /**
     *
     * @param origin 原始值
     * @param prefixNoMaskLen 前几位不脱敏
     * @param suffixNoMaskLen 后几位不脱敏
     * @param maskStr 脱敏值
     * @return 脱敏后的结果
     */
    public static String desValue(String origin,int prefixNoMaskLen,int suffixNoMaskLen,String maskStr){
        if(StringUtils.isBlank(origin)){
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for(int i=0,n=origin.length();i<n;i++){
            if(i<prefixNoMaskLen){
                builder.append(origin.charAt(i));
                continue;
            }
            if(i>(n-suffixNoMaskLen-1)){
                builder.append(origin.charAt(i));
                continue;
            }
            builder.append(maskStr);
        }
        return builder.toString();
    }
}
