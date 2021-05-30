package org.icpclive.webadmin.mainscreen.properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.icpclive.Config;

import java.io.IOException;
import java.util.Properties;

public class MainScreenProperties {
    private static final Logger log = LogManager.getLogger(MainScreenProperties.class);
    private final long latency;
    private final PersonProperties personProperties;
    private final TeamProperties teamProperties;
    private final CameraProperties cameraProperties;
    private final AdvertisementProperties advertisementProperties;
    private final BreakingNewsProperties breakingNewsProperties;
    private final CreepingLineProperties creepingLineProperties;
    private final PicturesProperties picturesProperties;
    private final VideoProperties videoProperties;
    private final long overlayedDelay;
    private final int pollTimeToShow;
    private final int wordTimeToShow;
    private final int factTimeToShow;


    public MainScreenProperties() {
        Properties properties = new Properties();
        try {
            properties = Config.loadProperties("mainscreen");
        } catch (IOException e) {
            log.error("Can't read mainscreen properties file", e);
        }
        this.latency = Long.parseLong(properties.getProperty("latency.time"));
        this.personProperties = new PersonProperties(properties, latency);
        this.teamProperties = new TeamProperties(properties);
        this.cameraProperties = new CameraProperties(properties);
        this.advertisementProperties = new AdvertisementProperties(properties, latency);
        this.breakingNewsProperties = new BreakingNewsProperties(properties, latency);
        this.creepingLineProperties = new CreepingLineProperties(properties);
        this.overlayedDelay = Long.parseLong(properties.getProperty("overlayed.delay", "4000"));
        this.pollTimeToShow = Integer.parseInt(properties.getProperty("poll.show.time", "20000"));
        this.wordTimeToShow = Integer.parseInt(properties.getProperty("word.statistics.word.show.time", "5000"));
        this.factTimeToShow = Integer.parseInt(properties.getProperty("fact.show.time", "10000"));
        this.picturesProperties = new PicturesProperties(properties);
        this.videoProperties = new VideoProperties(properties);

    }

    public long getLatency() {
        return latency;
    }

    public PersonProperties getPersonProperties() {
        return personProperties;
    }

    public TeamProperties getTeamProperties() {
        return teamProperties;
    }

    public CameraProperties getCameraProperties() {
        return cameraProperties;
    }

    public AdvertisementProperties getAdvertisementProperties() {
        return advertisementProperties;
    }

    public BreakingNewsProperties getBreakingNewsProperties() {
        return breakingNewsProperties;
    }

    public CreepingLineProperties getCreepingLineProperties() {
        return creepingLineProperties;
    }

    public long getOverlayedDelay() {
        return overlayedDelay;
    }

    public int getPollTimeToShow() {
        return pollTimeToShow;
    }

    public int getWordTimeToShow() {
        return wordTimeToShow;
    }

    public int getFactTimeToShow() {
        return factTimeToShow;
    }

    public PicturesProperties getPicturesProperties() {
        return picturesProperties;
    }

    public VideoProperties getVideoProperties() {
        return videoProperties;
    }
}
