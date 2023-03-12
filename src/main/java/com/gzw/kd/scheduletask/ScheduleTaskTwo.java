package com.gzw.kd.scheduletask;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.TimerTask;

/**
 * @author 高志伟
 */
@Component
@Slf4j
public class ScheduleTaskTwo extends TimerTask {


    @Override
    public void run() {
        log.info("------------------------------- gzw -------- timeTask-----------------------------------");
    }
}
