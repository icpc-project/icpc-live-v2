package org.icpclive.webadmin.mainscreen.properties;

import org.icpclive.events.ContestInfo;
import org.icpclive.events.EventsLoader;
import org.icpclive.events.PCMS.PCMSTeamInfo;
import org.icpclive.events.TeamInfo;
import org.icpclive.webadmin.MainScreenContextListener;

import java.util.*;

public class TeamProperties {
    private final Set<String> topTeamsId;
    private  int sleepTime;
    private final int automatedShowTime;
    private final String automatedInfo;
    private final ContestInfo contestInfo;
    private final TeamInfo[] teamInfos;


    public TeamProperties(final Properties properties) {
        this.sleepTime = Integer.parseInt(properties.getProperty("sleep.time"));
        this.automatedShowTime = Integer.parseInt(properties.getProperty("automated.show.time"));
        this.automatedInfo = properties.getProperty("automated.info");
        final String topTeams = properties.getProperty("top.teams", "");
        topTeamsId = new HashSet<>();
        topTeamsId.addAll(Arrays.asList(topTeams.split(",")));
        this.contestInfo = loadContestInfo();
        final String onsiteRegex = properties.getProperty("onsite.teams", ".*");
        this.teamInfos = reorderTeamInfos(contestInfo.getStandings(), onsiteRegex);


    }

    private ContestInfo loadContestInfo() {
        final EventsLoader eventsLoader = EventsLoader.getInstance();
        final Thread loaderThread = new Thread(eventsLoader);
        MainScreenContextListener.addThread(loaderThread);
        loaderThread.start();
        ContestInfo temporaryContestInfo;
        do {
            temporaryContestInfo = eventsLoader.getContestData();
        } while (temporaryContestInfo == null);
        return temporaryContestInfo;
    }

    private TeamInfo[] reorderTeamInfos(TeamInfo[] teamInfos, final String onsiteRegex) {
        if (teamInfos.length > 0 && teamInfos[0] instanceof PCMSTeamInfo) {
            int left = 0;
            for (TeamInfo teamInfo : teamInfos) {
                if (teamInfo.getAlias().matches(onsiteRegex)) {
                    teamInfos[left++] = teamInfo;
                }
            }
            teamInfos = Arrays.copyOf(teamInfos, left);
            Arrays.sort(teamInfos, Comparator.comparing(TeamInfo::getAlias));
        } else {
            Arrays.sort(teamInfos, Comparator.comparingInt(teamInfo -> Integer.parseInt(teamInfo.getAlias())));
        }
        return teamInfos;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public int getAutomatedShowTime() {
        return automatedShowTime;
    }

    public String getAutomatedInfo() {
        return automatedInfo;
    }

    public ContestInfo getContestInfo() {
        return contestInfo;
    }

    public TeamInfo[] getTeamInfos() {
        return teamInfos;
    }

    public Set<String> getTopTeamsId() {
        return topTeamsId;
    }

    public void setSleepTime(final int sleepTime) {
        this.sleepTime = sleepTime;
    }
}
