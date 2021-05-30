package org.icpclive.webadmin.utils;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.TextRenderer;
import org.icpclive.backend.player.urls.TeamUrls;
import org.icpclive.datapassing.TeamStatsData;
import org.icpclive.events.TeamInfo;
import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.mainscreen.team.TeamView;

import java.util.Set;

public class Utils {
    public static TextRenderer<TeamInfo> newTeamTextRenderer() {
        Set<String> topTeamsIds = MainScreenService.getProperties().getTeamProperties().getTopTeamsId();
        return new TextRenderer<>(teamInfo -> {
            if (topTeamsIds.contains(teamInfo.getAlias())) {
                return teamInfo.toString();
            }
            return teamInfo.toString();
        });
    }

    public static String getTeamStatus(String status) {
        String[] splitStatus = status.split("\0");
        String[] current = splitStatus[0].split(System.lineSeparator());
        String[] last = splitStatus[1].split(System.lineSeparator());
        if ("true".equals(current[1])) {
            boolean hasThisType = TeamView.STATISTICS_SHOW_TYPE.equals(current[2]);
            for (String type : TeamUrls.types) {
                hasThisType |= type.equals(current[2]);
            }
            if (!hasThisType) {
                return "Type " + current[2] + " does not exist";
            }
            long currentTime = System.currentTimeMillis() - Long.parseLong(current[0]);
            long sleepTime = MainScreenService.getInstance().getProperties().getTeamProperties().getSleepTime();
            if (currentTime > sleepTime) {
                return "Now showing " + current[2] + " of team " + current[3] +
                        " for " + (currentTime - sleepTime) / 1000 + " seconds";
            } else {
                String result = "buffering " + current[2] + " of team " + current[3] + " for " + currentTime / 1000 + " seconds";
                if ("false".equals(last[1])) {
                    return "Now " + result;
                }
                long lastTime = System.currentTimeMillis() - Long.parseLong(last[0]);
                return "Now showing " + last[2] + " of team " + last[3] + " for " + (lastTime - sleepTime) / 1000 + " seconds\n"
                        + "while " + result;
            }
        } else {
            TeamStatsData data = MainScreenService.getInstance().getTeamStatsData();
            if (data.isVisible()) {
                return "Not showing team view of team " + data.getTeamString() + ", but probably team stats is shown.";
            }
            return "No team view is shown";
        }
    }
}
