package org.icpclive.webadmin.mainscreen.properties;

import org.icpclive.webadmin.backup.BackUp;
import org.icpclive.webadmin.mainscreen.video.Video;

import java.util.Properties;

public class VideoProperties {
    private final String backUpFileName;
    private final BackUp<Video> backUp;


    public VideoProperties(final Properties properties) {
        this.backUpFileName = properties.getProperty("backup.videos", "backup-videos.txt");
        this.backUp = new BackUp<>(backUpFileName, Video.class);
    }

    public BackUp<Video> getBackUp() {
        return backUp;
    }
}
