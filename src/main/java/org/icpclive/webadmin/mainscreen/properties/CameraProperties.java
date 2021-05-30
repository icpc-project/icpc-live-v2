package org.icpclive.webadmin.mainscreen.properties;

import java.util.Properties;

public class CameraProperties {
    private final int count;
    private final String[] URLs;
    private final String[] names;


    public CameraProperties(final Properties properties) {
        this.count = Integer.parseInt(properties.getProperty("camera.number", "0"));
        this.URLs = new String[count];
        this.names = new String[count];
        for (int i = 0; i < count; i++) {
            URLs[i] = properties.getProperty("camera.url" + (i + 1));
            names[i] = properties.getProperty("camera.name" + (i + 1));
        }
    }

    public int getCount() {
        return count;
    }

    public String[] getURLs() {
        return URLs;
    }

    public String[] getNames() {
        return names;
    }
}
