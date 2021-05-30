package org.icpclive.datapassing;

import org.icpclive.webadmin.mainscreen.MainScreenService;

public class CameraData extends CachedData {
    public String cameraURL;
    private int cameraNumber;

    public CameraData() {
        this.timestamp = System.currentTimeMillis();
        this.cameraNumber = 0;
        this.cameraURL = MainScreenService.getProperties().
                getCameraProperties().getURLs()[0];
    }

    @Override
    public CameraData initialize() {
        CameraData data = MainScreenService.getInstance().getCameraData();
        timestamp = data.timestamp;
        cameraURL = data.cameraURL;
        return this;
    }

    public void recache() {
        Data.cache.refresh(CameraData.class);
    }

    public synchronized boolean setCameraNumber(int cameraNumber) {
        if (timestamp + MainScreenService.getProperties().getTeamProperties().getSleepTime() < System.currentTimeMillis()) {
            this.cameraNumber = cameraNumber;
            cameraURL = MainScreenService.getProperties().getCameraProperties().getURLs()[0];
            timestamp = System.currentTimeMillis();
            recache();
            return true;
        }
        return false;
    }

    public synchronized String cameraStatus() {
        return timestamp + "\n" + MainScreenService.getProperties().getCameraProperties().getNames()[cameraNumber];
    }
}
