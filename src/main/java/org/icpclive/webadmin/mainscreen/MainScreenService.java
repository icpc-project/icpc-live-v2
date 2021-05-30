package org.icpclive.webadmin.mainscreen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.icpclive.datapassing.*;
import org.icpclive.webadmin.MainScreenContextListener;
import org.icpclive.webadmin.mainscreen.picture.Picture;
import org.icpclive.webadmin.mainscreen.properties.MainScreenProperties;
import org.icpclive.webadmin.utils.LoopThread;

public class MainScreenService {
    private static final int SLEEP_INTERVAL = 1000;
    private static final Logger log = LogManager.getLogger(MainScreenService.class);
    private static MainScreenService instance;
    private final MainScreenProperties mainScreenProperties = new MainScreenProperties();
    private final AdvertisementData advertisementData;
    private final ClockData clockData;
    private final PersonData personData;
    private final StandingsData standingsData;
    private final TeamData teamData;
    private  CameraData cameraData;
    private final SplitScreenData splitScreenData;
    private final BreakingNewsData breakingNewsData;
    private final QueueData queueData;
    private final StatisticsData statisticsData;
    private final TeamStatsData teamStatsData;
    private final PollData pollData;
    private final WordStatisticsData wordStatisticsData;
    private final FrameRateData frameRateData;
    private final FactData factData;
    private final PictureData pictureData;
    private final PvPData pvpData;
    private final VideoData videoData;
    private final LocatorData locatorData;


    public static MainScreenService getInstance() {
        if (instance == null) {
            instance = new MainScreenService();
            final Thread updater = newMainScreenUpdater();
            updater.start();
            MainScreenContextListener.addThread(updater);

        }
        return instance;
    }

    private static LoopThread newMainScreenUpdater() {
        return new LoopThread(() -> {
            instance.update();
            try {
                Thread.sleep(SLEEP_INTERVAL);
            } catch (InterruptedException e) {
                log.error("Main screen thread was interrupted");
            }
        });
    }

    private MainScreenService() {
        advertisementData = new AdvertisementData();
        personData = new PersonData();
        standingsData = new StandingsData();
        teamData = new TeamData();
        //cameraData = new CameraData();
        clockData = new ClockData();
        splitScreenData = new SplitScreenData();
        breakingNewsData = new BreakingNewsData();
        queueData = new QueueData();
        statisticsData = new StatisticsData();
        teamStatsData = new TeamStatsData();
        pollData = new PollData();
        wordStatisticsData = new WordStatisticsData();
        frameRateData = new FrameRateData();
        factData = new FactData();
        pictureData = new PictureData();
        pvpData = new PvPData();
        videoData = new VideoData();
        locatorData = new LocatorData();
    }

    public void update() {
        advertisementData.update();
        personData.update();
        standingsData.update();
        breakingNewsData.update();
        teamData.update();
        wordStatisticsData.update();
        factData.update();
        pictureData.update();
        videoData.update();
    }

    public static MainScreenProperties getProperties() {
        return getInstance().mainScreenProperties;
    }


    public ClockData getClockData() {
        return clockData;
    }

    public AdvertisementData getAdvertisementData() {
        return advertisementData;
    }

    public PersonData getPersonData() {
        return personData;
    }

    public StandingsData getStandingsData() {
        return standingsData;
    }

    public TeamData getTeamData() {
        return teamData;
    }

    public CameraData getCameraData() {
        return cameraData;
    }

    public SplitScreenData getSplitScreenData() {
        return splitScreenData;
    }

    public BreakingNewsData getBreakingNewsData() {
        return breakingNewsData;
    }

    public QueueData getQueueData() {
        return queueData;
    }

    public StatisticsData getStatisticsData() {
        return statisticsData;
    }

    public TeamStatsData getTeamStatsData() {
        return teamStatsData;
    }

    public PollData getPollData() {
        return pollData;
    }

    public WordStatisticsData getWordStatisticsData() {
        return wordStatisticsData;
    }

    public FrameRateData getFrameRateData() {
        return frameRateData;
    }

    public FactData getFactData() {
        return factData;
    }

    public PictureData getPictureData() {
        return pictureData;
    }


    public PvPData getPvpData() {
        return pvpData;
    }

    public VideoData getVideoData() {
        return videoData;
    }

    public LocatorData getLocatorData() {
        return locatorData;
    }

}