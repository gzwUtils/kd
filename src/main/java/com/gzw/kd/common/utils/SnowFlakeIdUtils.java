package com.gzw.kd.common.utils;

import com.gzw.kd.common.generators.SnowFlakeIdGenerator;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

/**
 * @author gzw
 * @description：
 * @since：2023/5/11 16:13
 */
public class SnowFlakeIdUtils {

    public static Long generatorId() {
        return new SnowFlakeIdGenerator(getWorkId(), getDataCenterId()).nextId();
    }

    /**
     * workId使用IP生成
     * @return workId
     */
    private static Long getWorkId() {
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for (int b : ints) {
                sums = sums + b;
            }
            return (long) (sums % 32);
        }
        catch (UnknownHostException e) {
            // 失败就随机
            return RandomUtils.nextLong(0, 31);
        }
    }


    /**
     * dataCenterId使用hostName生成
     * @return dataCenterId
     */
    private static Long getDataCenterId() {
        try {
            String hostName = SystemUtils.getHostName();
            int[] ints = StringUtils.toCodePoints(hostName);
            int sums = 0;
            for (int i: ints) {
                sums = sums + i;
            }
            return (long) (sums % 32);
        }
        catch (Exception e) {
            // 失败就随机
            return RandomUtils.nextLong(0, 31);
        }
    }
}
