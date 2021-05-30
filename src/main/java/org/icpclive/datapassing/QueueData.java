package org.icpclive.datapassing;

import org.icpclive.webadmin.mainscreen.MainScreenService;

public class QueueData extends CachedData {
    public QueueData() {
        isVisible = true;
    }

    public void recache() {
        Data.cache.refresh(QueueData.class);
    }

    public synchronized void setVisible(boolean visible) {
        timestamp = System.currentTimeMillis();
        isVisible = visible;
        recache();
    }

    public boolean isQueueVisible() {
        return isVisible;
    }

    public QueueData initialize() {
        QueueData data = MainScreenService.getInstance().getQueueData();
        this.timestamp = data.timestamp;
        this.isVisible = data.isVisible;

        return this;
    }

    private boolean isVisible;
}
