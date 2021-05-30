package org.icpclive.datapassing;

import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.mainscreen.properties.MainScreenProperties;
import org.icpclive.webadmin.mainscreen.statistics.WordStatistics;

/**
 * Created by Meepo on 4/16/2017.
 */
public class WordStatisticsData extends CachedData {
    @Override
    public WordStatisticsData initialize() {
        WordStatisticsData wordStatisticsData = MainScreenService.getInstance().getWordStatisticsData();
        isVisible = wordStatisticsData.isVisible;
        word = wordStatisticsData.word;
        return this;
    }

    public String getOverlayError() {
        return "You have to wait while word statistics is shown";
    }

    public String checkOverlays() {
        if (MainScreenService.getInstance().getFactData().isVisible) {
            return MainScreenService.getInstance().getFactData().getOverlayError();
        }
        return null;
    }

    public synchronized String setWordVisible(WordStatistics word) {
        String check = checkOverlays();
        if (check != null) {
            return check;
        }

        if (isVisible) {
            return "The word statistics is currently visible";
        }
        this.word = word;
        timestamp = System.currentTimeMillis();
        isVisible = true;
        recache();
        return null;
    }

    public void hide() {
        isVisible = false;
        recache();
    }

    public synchronized void update() {
        if (!isVisible) {
            return;
        }
        MainScreenProperties properties = MainScreenService.getProperties();
        long now = System.currentTimeMillis();
        if (now - timestamp >= properties.getWordTimeToShow()) {
            isVisible = false;
            recache();
        }
        recache();
    }

    public String toString() {
        return isVisible ? "Showing statistics of word " + word.getWord() +
                " for " + Math.max(0, timestamp +
                MainScreenService.getProperties().getWordTimeToShow() -
                System.currentTimeMillis()) / 1000 + " more seconds" : "Word statistics is not shown";
    }

    public void recache() {
        Data.cache.refresh(WordStatisticsData.class);
    }

    public boolean isVisible;
    public WordStatistics word;

}
