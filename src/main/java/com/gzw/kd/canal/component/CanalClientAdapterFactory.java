package com.gzw.kd.canal.component;
import com.gzw.kd.canal.adapter.MessageAdapter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.util.Map;
import java.util.Optional;

/**
 * @author gzw
 */
@Component
public class CanalClientAdapterFactory implements ApplicationContextAware {

    private Map<String, MessageAdapter> messageAdapters;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        Map<String, MessageAdapter> map = Optional.of(ac.getBeansOfType(MessageAdapter.class)).orElse(CollectionUtils.newHashMap(16));
        messageAdapters = CollectionUtils.newHashMap(map.size());
        map.forEach((k, v) -> messageAdapters.put(v.getName().toLowerCase(), v));
    }

    public Map<String, MessageAdapter> getMessageAdapters() {
        return messageAdapters;
    }
}
