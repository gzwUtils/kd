package com.gzw.kd.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gaozhiwei
 */

@SuppressWarnings("all")
public class RegexUtils {

    /**
     * 是否包含正则表达式
     *
     * @return
     */
    public static boolean isContainRegexChar(String str) {
        String pattern = "^.*[\\^|$|\\\\|'|\"|.|\\[|\\]|*|+|?|{|}|\\||(|)|,|<|>|:|#|!|/].*$";
        return str.matches(pattern);
    }

    /**
     * 获取正则匹配数据
     *
     * @param content
     * @param regexContent
     * @return
     */
    public static String getRegexStr(String content, String regexContent) {
        Pattern cityPattern = Pattern.compile(regexContent);
        Matcher matcher = cityPattern.matcher(content);
        String fitStr = null;
        while (matcher.find()) {
            fitStr = matcher.group();
            if (fitStr != null) {
                break;
            }
        }
        return fitStr;
    }


    public static String getPhone(String question) {
        Pattern phone_mather = Pattern.compile("1\\d{10}");
        Matcher matcher = phone_mather.matcher(question);
        if (matcher.find()) {
            String phone = matcher.group();
            return phone;
        }
        return null;
    }
}
