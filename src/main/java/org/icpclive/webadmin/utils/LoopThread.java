package org.icpclive.webadmin.utils;

public class LoopThread extends Thread {

    public LoopThread(final Runnable runnable) {
        super(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                runnable.run();
            }
        });
    }
}
