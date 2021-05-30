package org.icpclive.webadmin.mainscreen.picture;

public class Picture {
    private String caption;
    private String path;

    public Picture(final String caption, final String path) {
        this.caption = caption;
        this.path = path;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(final String caption) {
        this.caption = caption;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }
}
