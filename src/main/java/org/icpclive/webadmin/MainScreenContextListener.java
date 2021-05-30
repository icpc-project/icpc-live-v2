package org.icpclive.webadmin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.icpclive.datapassing.DataLoader;
import org.icpclive.events.EventsLoader;
import org.icpclive.webadmin.creepingline.MessageService;
import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.mainscreen.loaders.TwitchLoader;
import org.icpclive.webadmin.mainscreen.loaders.TwitterLoader;
import org.icpclive.webadmin.utils.LoopThread;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


@WebListener
public class MainScreenContextListener implements ServletContextListener {
    private static final Logger log = LogManager.getLogger(MainScreenContextListener.class);
    private static final BlockingQueue<Thread> runningThreads = new LinkedBlockingQueue<>();
    private final int SLEEP_INTERVAL = 1000;

    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        DataLoader.free();
        runningThreads.forEach(thread -> {
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }
        });
    }

    @Override
    public void contextInitialized(final ServletContextEvent event) {
        LoopThread frontendDataLoader = newFrontendDataLoader();
        frontendDataLoader.setDaemon(true);
        frontendDataLoader.start();

        MainScreenService.getInstance();
        EventsLoader.getInstance();
        MessageService.getInstance();
        TwitchLoader.start();
        TwitterLoader.start();
        addThread(frontendDataLoader);

    }

    private LoopThread newFrontendDataLoader() {
        return new LoopThread(() -> {
            DataLoader.iterateFrontend();
            try {
                Thread.sleep(SLEEP_INTERVAL);
            } catch (InterruptedException e) {
                log.error("Data loader thread was interrupted", e);
            }
        });
    }

    public static void addThread(final Thread thread) {
        runningThreads.add(thread);
    }
}
