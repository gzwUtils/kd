package com.gzw.kd.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import static cn.hutool.core.net.NetUtil.LOCAL_IP;
import static com.gzw.kd.common.Constants.*;


/**
 * @author gzw
 */
@SuppressWarnings("all")
@Slf4j
public class AddressUtil {

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StrUtil.isEmpty(ip) || STRING_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isEmpty(ip) || STRING_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isEmpty(ip) || STRING_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isEmpty(ip) || STRING_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isEmpty(ip) || STRING_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StrUtil.isEmpty(ip) || STRING_UNKNOWN.equalsIgnoreCase(ip)) {
            if (Objects.nonNull(request.getRemoteAddr())) {
                ip = request.getRemoteAddr();
            }
        }
        if (ip != null) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            ip = ip.split(",")[0];
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }


    /**
     * 根据ip获取归属地
     * @param ip
     * @return
     */
    public static String getAddress(String ip) {
        // IP地址查询
        final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp?ip="+ip;

        // 未知地址
        final String UNKNOWN = "未知";
        // 内网不查询
        if (internalIp(ip)) {
            return "内网";
        }
        try {
            String rspStr = HttpUtil.get(IP_URL);
            if (StringUtils.isEmpty(rspStr)) {
                log.error("获取地理位置异常 {}", ip);
                return UNKNOWN;
            }
            String substring = rspStr.substring(rspStr.lastIndexOf("{"), rspStr.indexOf("}")+1);
            JSONObject jsonObject = JSON.parseObject(substring);
            String addr = jsonObject.getString("addr");
            return addr;
        } catch (Exception e) {
            log.error("获取地理位置异常 {}", ip);
        }
        return UNKNOWN;
    }


    /**
     * 查看是否内部ip
     * @param ip
     * @return
     */
    public static boolean internalIp(String ip) {
        boolean res = false;
        byte[] addr = textToNumericFormatV4(ip);
        if (addr != null && ip != null) {
            res = internalIp(addr) || LOCAL_IP.equals(ip);
        }
        return res;
    }

    private static boolean internalIp(byte[] addr) {
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        final byte SECTION_1 = 0x0A;
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        boolean flag = false;
        switch (b0) {
            case SECTION_1:
                flag = true;
                break;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                    flag = true;
                }
                break;
            case SECTION_5:
                if (b1 == SECTION_6) {
                    flag = true;
                }
                break;
            default:
                break;
        }
        return flag;
    }

    /**
     * 将IPv4地址转换成字节
     *IPv4地址
     * @param text
     * @return byte 字节
     */
    public static byte[] textToNumericFormatV4(String text) {
        if (text.length() == 0) {
            return null;
        }

        byte[] bytes = new byte[4];
        String[] elements = text.split("\\.", -1);
        try {
            long l;
            int i;
            switch (elements.length) {
                case 1:
                    l = Long.parseLong(elements[0]);
                    if ((l < 0L) || (l > 4294967295L)) {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l >> 24 & 0xFF);
                    bytes[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 2:
                    l = Integer.parseInt(elements[0]);
                    if ((l < 0L) || (l > 255L)) {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l & 0xFF);
                    l = Integer.parseInt(elements[1]);
                    if ((l < 0L) || (l > 16777215L)) {
                        return null;
                    }
                    bytes[1] = (byte) (int) (l >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 3:
                    for (i = 0; i < 2; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L)) {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    l = Integer.parseInt(elements[2]);
                    if ((l < 0L) || (l > 65535L)) {
                        return null;
                    }
                    bytes[2] = (byte) (int) (l >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 4:
                    for (i = 0; i < 4; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L)) {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    break;
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            log.error("数字格式化异常",e);
            return null;
        }
        return bytes;
    }


}
