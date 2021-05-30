package org.icpclive.webadmin.mainscreen.properties;

import org.icpclive.webadmin.backup.BackUp;
import org.icpclive.webadmin.mainscreen.caption.Advertisement;

import java.util.Properties;

public class AdvertisementProperties {
    private final String backUpFileName;
    private final long time;
    private final BackUp<Advertisement> backUp;


    public AdvertisementProperties(final Properties properties, final long latency) {
        this.backUpFileName = properties.getProperty("backup.advertisements");
        this.time = Long.parseLong(properties.getProperty("advertisement.time")) + latency;
        this.backUp = new BackUp<>(backUpFileName, Advertisement.class);
    }

    public String getBackUpFileName() {
        return backUpFileName;
    }

    public long getTime() {
        return time;
    }

    public BackUp<Advertisement> getBackUp() {
        return backUp;
    }
}
