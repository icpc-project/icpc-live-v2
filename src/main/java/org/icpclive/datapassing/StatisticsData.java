package org.icpclive.datapassing;

import org.icpclive.webadmin.mainscreen.MainScreenService;

public class StatisticsData extends CachedData {

    public void recache() {
        Data.cache.refresh(StatisticsData.class);
    }

    public synchronized String setVisible(boolean visible) {
        delay = 0;
        if (visible) {
            String outcome = checkOverlays();
            if (outcome != null) {
                return outcome;
            }
            switchOverlaysOff();
        }
        timestamp = System.currentTimeMillis();
        isVisible = visible;
        recache();
        return null;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public StatisticsData initialize() {
        StatisticsData data = MainScreenService.getInstance().getStatisticsData();
        this.timestamp = data.timestamp;
        this.isVisible = data.isVisible;
        this.delay = data.delay;

        return this;
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
        if (mainScreenService.getPollData().isVisible) {
            mainScreenService.getPollData().hide();
            turnOff = true;
        }
        if (mainScreenService.getPictureData().isVisible()) {
            mainScreenService.getPictureData().hide();
            turnOff = true;
        }
        if (turnOff) {
            delay = MainScreenService.getProperties().getOverlayedDelay();
        } else {
            delay = 0;
        }
    }

    @Override
    public void hide() {
        delay = 0;
        setVisible(false);
    }

    private boolean isVisible;
}
