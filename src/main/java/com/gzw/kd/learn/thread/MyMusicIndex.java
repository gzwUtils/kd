package com.gzw.kd.learn.thread;

import java.util.concurrent.locks.ReentrantLock;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author 高志伟
 */
@Slf4j
public class MyMusicIndex extends JFrame implements Runnable {

    private static final String[] MUSICS = {"凹凸.mp3"};

    private static Player player;

    private static final ReentrantLock REENTRANT_LOCK = new ReentrantLock();


    @Override
    public void run() {

        REENTRANT_LOCK.lock();
        try {
            if (null != player) {
                player.close();
            }
            long mills = System.currentTimeMillis();
            mills = mills % MUSICS.length;
            log.info("music play start----------------------------------------------------{} ", MUSICS[(int) mills]);
            ClassPathResource resource = new ClassPathResource("/static/music/" + MUSICS[(int) mills]);
            InputStream stream = resource.getInputStream();
            player = new Player(stream);
            player.play();
            log.info("music play end---------------------------------------------------- ");
            Thread.sleep(60000);
        } catch (JavaLayerException | InterruptedException | IOException e1) {
            log.error("music play error ", e1);
        } finally {
            REENTRANT_LOCK.unlock();
            if (null != player) {
                player.close();
            }
        }
    }
}
