package org.icpclive.webadmin.mainscreen.news;

import org.icpclive.events.RunInfo;
import org.icpclive.webadmin.mainscreen.MainScreenService;

public class BreakingNews {
    private String outcome;
    private final String problem;
    private final int team;
    private long timestamp;
    private final int runId;

    public BreakingNews(final String outcome, final String problem, final int team, final long timestamp, final int runId) {
        this.outcome = outcome;
        this.problem = problem;
        this.team = team;
        this.timestamp = timestamp;
        this.runId = runId;
    }

    public void update(final RunInfo runInfo) {
        if (runInfo != null) {
            outcome = runInfo.getResult();
            timestamp = runInfo.getTime();
        }
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(final String outcome) {
        this.outcome = outcome;
    }

    public String getProblem() {
        return problem;
    }

    public int getTeamId() {
        return team;
    }

    public String getTeam() {
        return MainScreenService.getProperties().getTeamProperties().getContestInfo().getParticipant(team - 1).getShortName();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getRunId() {
        return runId;
    }

    public BreakingNews clone() {
        return new BreakingNews(outcome, problem, team, timestamp, runId);
    }
}
