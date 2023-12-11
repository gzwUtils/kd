package com.gzw.kd.learn.writes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("all")
@Slf4j
public class InitLearn implements SmartInitializingSingleton, SmartLifecycle {

    private boolean isRunning = false;


    @Override
    public void afterSingletonsInstantiated() {
            log.info("init smartInitializingSingleton");
    }


    @Override
    public void start() {
        log.info("init  start  smartLifecycle");

        isRunning = true;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop() {
        log.info("init  stop  smartLifecycle");

        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }
}
