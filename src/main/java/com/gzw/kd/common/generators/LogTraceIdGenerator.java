package com.gzw.kd.common.generators;

import com.gzw.kd.common.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.UUID;
import static com.gzw.kd.common.Constants.TRACE_ID_FLAG;

@Component
@SuppressWarnings("all")
@Slf4j
public class LogTraceIdGenerator implements IdGenerator{


    @Override
    public String generate(Integer length) throws GlobalException {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "").substring(0, length);
        return TRACE_ID_FLAG+uuid;
    }
}
