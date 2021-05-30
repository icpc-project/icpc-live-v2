package org.icpclive.webadmin.mainscreen.properties;

import java.util.Properties;

public class BreakingNewsProperties {
    private final long timeToShow;
    private final int runCount;
    private final String backUpFilename;
    private final String patternsFileName;

    public BreakingNewsProperties(final Properties properties, final long latency) {
        this.timeToShow = Long.parseLong(properties.getProperty("breakingnews.time")) + latency;
        this.runCount = Integer.parseInt(properties.getProperty("breakingnews.runs.number"));
        this.backUpFilename = properties.getProperty("backup.breakingnews");
        this.patternsFileName = properties.getProperty("breakingnews.patterns.filename");
    }

    public long getTimeToShow() {
        return timeToShow;
    }

    public int getRunCount() {
        return runCount;
    }

    public String getBackUpFilename() {
        return backUpFilename;
    }

    public String getPatternsFileName() {
        return patternsFileName;
    }
}
