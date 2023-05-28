package com.gzw.kd.handler;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description：
 * @since：2023/5/24 14:48
 */

@Component
public class HandlerHolder {

    private final Map<Integer,Handler> handlers  = new HashMap<>(16);


    public void putHandler(Integer channelCode, Handler handler){
        handlers.put(channelCode,handler);
    }

    public Handler getHandler(Integer channelCode){
      return   handlers.get(channelCode);
    }
}
