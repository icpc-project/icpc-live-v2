package org.icpclive.datapassing;

import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.mainscreen.picture.Picture;
import org.icpclive.webadmin.mainscreen.video.Video;

import java.util.List;

public class VideoData extends CachedData {
    public VideoData() {

    }

    public CachedData initialize() {
        VideoData data = MainScreenService.getInstance().getVideoData();
        this.timestamp = data.timestamp;
        this.video = data.video;
        this.delay = data.delay;
        return this;
    }

    public Video video;

    public synchronized String setVisible(Video picture) {
        String error = checkOverlays();
        if (error != null) {
            return error;
        }
        if (this.video != null) {
            return "Please hide the previous picture first";
        }
        this.video = picture;
        this.timestamp = System.currentTimeMillis();
        switchOverlaysOff();
        recache();
        return null;
    }

    public synchronized void hide() {
        this.video = null;
        this.timestamp = System.currentTimeMillis();
        delay = 0;
        recache();
    }

    public synchronized boolean isVisible() {
        return video != null;
    }

    private void recache() {
        Data.cache.refresh(VideoData.class);
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
        return "You need to hide video first";
    }

    public void update() {

    }

    public synchronized String toString() {
        return video == null ? "No video is shown" : "Video is now shown";
    }

    public synchronized void setNewCaption(final String caption, final Video item) {
        synchronized (MainScreenService.getProperties().getVideoProperties().getBackUp().getData()) {
            item.setCaption(caption);
        }
    }
    public void addVideo(Video video) {
        MainScreenService.getProperties().getVideoProperties().getBackUp().addItemAt(0, video);
    }

    public List<Video> getContainer() {
        return MainScreenService.getProperties().getVideoProperties().getBackUp().getData();
    }
}
