package org.icpclive.webadmin.mainscreen.properties;

import org.icpclive.webadmin.backup.BackUp;
import org.icpclive.webadmin.mainscreen.picture.Picture;

import java.util.Properties;

public class PicturesProperties {
    private final String backUpFileName;
    private final BackUp<Picture> backUp;


    public PicturesProperties(final Properties properties) {
        this.backUpFileName = properties.getProperty("backup.pictures", "backup-pictures.txt");
        this.backUp = new BackUp<>(backUpFileName, Picture.class);
    }

    public BackUp<Picture> getBackUp() {
        return backUp;
    }
}
