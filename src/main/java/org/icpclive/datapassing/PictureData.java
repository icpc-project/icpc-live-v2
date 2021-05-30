package org.icpclive.datapassing;

import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.mainscreen.picture.Picture;

import java.util.List;

/**
 * Created by Meepo on 11/29/2018.
 */
public class PictureData extends CachedData {
    public PictureData() {

    }

    public CachedData initialize() {
        PictureData data = MainScreenService.getInstance().getPictureData();
        this.timestamp = data.timestamp;
        this.picture = data.picture;
        this.delay = data.delay;
        return this;
    }

    public Picture picture;

    public synchronized String setVisible(Picture picture) {
        String error = checkOverlays();
        if (error != null) {
            return error;
        }
        if (this.picture != null) {
            return "Please hide the previous picture first";
        }
        this.picture = picture;
        this.timestamp = System.currentTimeMillis();
        switchOverlaysOff();
        recache();
        return null;
    }

    public synchronized void hide() {
        this.picture = null;
        this.timestamp = System.currentTimeMillis();
        delay = 0;
        recache();
    }

    public synchronized boolean isVisible() {
        return picture != null;
    }

    private void recache() {
        Data.cache.refresh(PictureData.class);
    }


    public String checkOverlays() {
        MainScreenService mainScreenService = MainScreenService.getInstance();
        if (mainScreenService.getTeamData().isVisible) {
            return mainScreenService.getTeamData().getOverlayError();
        }
        if (mainScreenService.getPvpData().isVisible()) {
            return mainScreenService.getPvpData().getOverlayError();
        }
        return null;
    }

    public void switchOverlaysOff() {
        MainScreenService mainScreenService = MainScreenService.getInstance();
        boolean turnOff = false;
        if (mainScreenService.getStandingsData().isVisible &&
            mainScreenService.getStandingsData().isBig) {
            mainScreenService.getStandingsData().hide();
            turnOff = true;
        }
        if (mainScreenService.getStatisticsData().isVisible()) {
            mainScreenService.getStatisticsData().hide();
            turnOff = true;
        }
        if (mainScreenService.getPollData().isVisible) {
            mainScreenService.getPollData().hide();
            turnOff = true;
        }
        if (turnOff) {
            delay = MainScreenService.getProperties().getOverlayedDelay();
        } else {
            delay = 0;
        }
    }

    public String getOverlayError() {
        return "You need to hide picture first";
    }

    public void update() {

    }
    public void addPicture(Picture item) {
        MainScreenService.getProperties().getPicturesProperties().getBackUp().addItemAt(0, item);
    }

    public synchronized String toString() {
        return picture != null ? "The analytics picture is now showing" : "No analytics picture now";
    }

    public synchronized void setNewCaption(final String caption, final Picture item) {
        synchronized (MainScreenService.getProperties().getPicturesProperties().getBackUp().getData()) {
            item.setCaption(caption);
        }
    }

    public List<Picture> getContainer() {
        return MainScreenService.getProperties().getPicturesProperties().getBackUp().getData();
    }
}
