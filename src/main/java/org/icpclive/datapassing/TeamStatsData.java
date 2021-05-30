package org.icpclive.datapassing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.icpclive.events.TeamInfo;
import org.icpclive.webadmin.mainscreen.MainScreenService;

public class TeamStatsData extends CachedData {
    private static final Logger log = LogManager.getLogger(TeamStatsData.class);

    public TeamStatsData() {
    }

    @Override
    public TeamStatsData initialize() {
        TeamStatsData data = MainScreenService.getInstance().getTeamStatsData();
        this.timestamp = data.timestamp;
        this.isVisible = data.isVisible;
        this.teamId = data.teamId;
        this.delay = data.delay;

        return this;
    }

    public void recache() {
        Data.cache.refresh(TeamStatsData.class);
    }

    public void switchOverlaysOff() {
        if (MainScreenService.getInstance().getStandingsData().isVisible &&
                !MainScreenService.getInstance().getStandingsData().isBig) {
            MainScreenService.getInstance().getStandingsData().hide();
        }
    }

    public String getOverlayError() {
        return "You should close team view first!";
    }

    public synchronized void setVisible(boolean visible, TeamInfo teamInfo) {
        timestamp = System.currentTimeMillis();
        isVisible = visible;
        if (visible) {
            currentTeamValue = teamInfo.getName();
            teamName = teamInfo.getName();
            teamId = teamInfo.getId();
        }
        switchOverlaysOff();
        recache();
    }

    private synchronized void hideInfo() {
        timestamp = System.currentTimeMillis();
        isVisible = false;

        recache();
    }


    public synchronized boolean isVisible() {
        return isVisible;
    }

    public synchronized String getTeamString() {
        return currentTeamValue;
    }

    public synchronized int getTeamId() {
        return teamId;
    }

    public synchronized void update() {
    }

    public boolean isVisible;
    private String currentTeamValue;
    private String teamName;
    private int teamId = -1;
}
