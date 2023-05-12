package com.gzw.kd.common.generators;
import cn.hutool.core.util.RandomUtil;
import com.gzw.kd.common.exception.GlobalException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Data;

/**
 * @author gzw
 * @description：
 * @since：2023/5/11 17:43
 */
@Data
public class SnowIdGenerator implements IdGenerator{


    /**
     * 业务类型
     */
    private  String bizTypePrefix;

    /**
     * 日期格式
     */

    private  String dateFormat ;


    private static AtomicInteger count = new AtomicInteger(20_000);


    public SnowIdGenerator(String bizTypePrefix, String dateFormat) {
        this.bizTypePrefix = bizTypePrefix;
        this.dateFormat = dateFormat;
    }

    @Override
    public String generate(Integer length) throws GlobalException {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        count.incrementAndGet();
        return this.bizTypePrefix+format.format(new Date()) + RandomUtil.randomNumbers(length)+count.get();
    }
}
