package com.gzw.kd.common.generators;
import com.gzw.kd.common.exception.GlobalException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author gzw
 * @description：
 * @since：2023/5/11 17:43
 */
@Data
public class SnowIdGenerator{


    /**业务类型*/
    private  String bizTypePrefix;

    /** 日期格式*/

    private  String dateFormat;

    /** 秒内序列(0~16384) */
    private long sequence = 12L;

    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;


    /** 序列在id中占的位数 */
    private final long sequenceBits = 6L;

    /** 生成序列的掩码 */
    private final long sequenceMask = ~(-1L << sequenceBits);


    private static AtomicLong START_ = new AtomicLong(2000_0);


    public SnowIdGenerator(String bizTypePrefix, String dateFormat) {
        this.bizTypePrefix = bizTypePrefix;
        if(StringUtils.isBlank(dateFormat)){
            this.dateFormat   = "yyMMdd";
        } else {
            this.dateFormat   = dateFormat;
        }
    }



    public  String generate() throws GlobalException {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return this.bizTypePrefix+format.format(new Date())+nextId()+START_.get();
    }


    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */

    @SuppressWarnings("all")
    public synchronized long nextId() {
        long timestamp = timeGen();
        START_.incrementAndGet();
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个秒,获得新的时间戳
                timestamp = tilNextIs(lastTimestamp);
            }
        }
        // 时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        // 上次生成ID的时间截
        lastTimestamp = timestamp;

        return (timestamp << sequenceBits) | sequence;
    }

    /**
     * 阻塞到下一秒，直到获得新的时间戳
     *
     * @param lastTimestamp
     *            上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextIs(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以秒为单位的当前时间
     *
     * @return 当前时间(秒)
     */
    protected long timeGen(){
        return System.currentTimeMillis() / 1000L;
    }

}
