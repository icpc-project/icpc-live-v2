package org.icpclive.datapassing;

import org.icpclive.webadmin.mainscreen.MainScreenService;

/**
 * Created by Meepo on 4/12/2018.
 */
public class FactData extends CachedData {
    public boolean isVisible = false;
    public String factTitle = "";
    public String factText = "";

    @Override
    public FactData initialize() {
        FactData factData = MainScreenService.getInstance().getFactData();
        timestamp = factData.timestamp;
        isVisible = factData.isVisible;
        factTitle = factData.factTitle;
        factText = factData.factText;
        return this;
    }
    public String getOverlayError() {
        return "You have to wait while fact is shown";
    }

    public String checkOverlays() {
        if (MainScreenService.getInstance().getWordStatisticsData().isVisible) {
            return MainScreenService.getInstance().getWordStatisticsData().getOverlayError();
        }
        return null;
    }

    public synchronized String show(String factTitle, String factText) {
        String check = checkOverlays();
        if (check != null) {
            return check;
        }
        if (isVisible) {
            return "The fact data is already shown right now";
        }
        timestamp = System.currentTimeMillis();
        this.factTitle = factTitle;
        this.factText = factText;
        isVisible = true;
        recache();
        return null;
    }

    public synchronized void hide() {
        isVisible = false;
        timestamp = System.currentTimeMillis();
        recache();
    }

    public synchronized void update() {
        if (!isVisible) {
            return;
        }

        if (System.currentTimeMillis() - timestamp >
                MainScreenService.getProperties().getFactTimeToShow()) {
            hide();
        }
    }

    public String toString() {
        return isVisible ? "Showing fact for " +
                Math.max(0, MainScreenService.getProperties().getFactTimeToShow() -
                                (System.currentTimeMillis() - timestamp)) / 1000 +
                " more seconds" : "Fact is not shown";
    }

    public void recache() {
        Data.cache.refresh(FactData.class);
    }
}
