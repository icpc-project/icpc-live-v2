package org.icpclive.datapassing;

import org.icpclive.events.TeamInfo;
import org.icpclive.webadmin.mainscreen.MainScreenService;

/**
 * Created by Aksenov239 on 21.11.2015.
 */
public class BreakingNewsData extends CachedData {
    public BreakingNewsData initialize() {
        BreakingNewsData data = MainScreenService.getInstance().getBreakingNewsData();
        this.timestamp = data.timestamp;
        this.isVisible = data.isVisible;
        this.isLive = data.isLive;
        this.teamId = data.teamId;
        this.problemId = data.problemId;
        this.runId = data.runId;
        this.infoType = data.infoType;
        this.newsMessage = data.newsMessage;
        this.delay = data.delay;
        this.sleepTime = data.sleepTime;
        return this;
    }

    public void recache() {
        Data.cache.refresh(BreakingNewsData.class);
    }

    public String checkOverlays() {
        if (MainScreenService.getInstance().getWordStatisticsData().isVisible) {
            return MainScreenService.getInstance().getWordStatisticsData().getOverlayError();
        }
        if (MainScreenService.getInstance().getStandingsData().isVisible &&
                !MainScreenService.getInstance().getStandingsData().isBig) {
            return MainScreenService.getInstance().getStandingsData().getOverlayError();
        }
        return null;
    }

    public synchronized String setNewsVisible(boolean visible, String type, boolean isLive, String newsMessage, int teamId, int problemId, int runId) {
        String check = checkOverlays();
        if (check != null) {
            return check;
        }

        if (!visible && !isVisible) {
            return "You cannot hide breaking news, it is not shown";
        }
        if (visible && isVisible) {
            return "You need to wait while current breaking news is shown";
        }

        this.isVisible = visible;

        if (visible) {
            TeamInfo teamInfo = MainScreenService.getProperties().getTeamProperties().getContestInfo().getParticipant(teamId);
            this.teamId = teamId;
            this.problemId = problemId;
            this.newsMessage = newsMessage;
            this.runId = runId;

            teamName = teamInfo.getName();
            infoType = type;
            this.isLive = isLive;
        }

        this.timestamp = System.currentTimeMillis();
        recache();

        return null;
    }

    public void update() {
        boolean change = false;
        synchronized (breakingNewsLock) {
            long time = timestamp +
                    MainScreenService.getProperties().getBreakingNewsProperties().getTimeToShow() +
                    MainScreenService.getProperties().getTeamProperties().getSleepTime();
            if (System.currentTimeMillis() > time) {
                isVisible = false;
                change = true;
            }
        }
        if (change) {
            recache();
        }
    }

    public String toString() {
        return timestamp + "\n" + isVisible + "\n" + infoType + "\n" + isLive + "\n" + teamName + "\n" + (char) ('A' + problemId);
    }

    public String getStatus() {
        if (isVisible) {
            String status = "Breaking news (%s) are shown for team %s and problem %c for %d seconds";

            long time = (timestamp +
                    MainScreenService.getProperties().getBreakingNewsProperties().getTimeToShow() +
                    MainScreenService.getProperties().getTeamProperties().getSleepTime()
                    - System.currentTimeMillis()) / 1000;
            String type = isLive ? infoType : "record";

            return String.format(status, type, teamName, (char) ('A' + problemId), time);
        } else {
            return "Breaking news aren't shown";
        }

    }

    public String getOverlayError() {
        return "You need to wait while breaking news finishes";
    }

    public boolean isVisible;
    public int teamId;
    public String teamName;
    public int problemId;
    public int runId;
    public String infoType;
    public boolean isLive;
    public String newsMessage;
    public int sleepTime;

    private static int lastShowedRun = 0;

    final private Object breakingNewsLock = new Object();
}
