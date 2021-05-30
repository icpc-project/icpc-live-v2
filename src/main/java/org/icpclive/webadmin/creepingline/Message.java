package org.icpclive.webadmin.creepingline;

public class Message {
    private  String message;

    private final long creationTime;
    private final long endTime;
    private final long duration;
    private  boolean advertisement;
    private String source;

    public Message(final String message, final long creationTime, final long duration, final boolean advertisement) {
        this.message = message;
        this.creationTime = creationTime;
        this.endTime = creationTime + duration;
        this.duration = duration / 1000;
        this.advertisement = advertisement;
    }

    public Message(final String message, final long creationTime, final long duration, final boolean advertisement, final String source) {
        this(message, creationTime, duration, advertisement);
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isAdvertisement() {
        return advertisement;
    }

    public String getSource() {
        return source;
    }

    public Message clone() {
        return new Message(message, creationTime, endTime - creationTime, advertisement);
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setAdvertisement(final boolean advertisement) {
        this.advertisement = advertisement;
    }
}
