package org.icpclive.webadmin.mainscreen.loaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.icpclive.Config;
import org.icpclive.webadmin.MainScreenContextListener;
import org.icpclive.webadmin.creepingline.MessageService;
import org.icpclive.webadmin.mainscreen.polls.PollsService;
import org.icpclive.webadmin.mainscreen.statistics.WordStatisticService;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class TwitterLoader implements Runnable {
    private static final Logger logger = LogManager.getLogger(TwitterLoader.class);
    private TwitterStream twitterStream;
    private static Twitter twitter;
    private static TwitterLoader instance;
    private String mainHashTag;
    private String pollHashTag;


    public static TwitterLoader getInstance() {
        return instance;
    }

    public static void start() {
        if (instance == null) {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("ufPvAsmjiMgdoqjMUNEzQ")
                    .setOAuthConsumerSecret("2FixWAc64f5m2R0KyN6okd1DWHeaa1qThgHbzLYzHM4")
                    .setOAuthAccessToken("450069411-sjzhMx4PZPp3CiZUnzISyTOgD0hmhgNpAv8cZeiR")
                    .setOAuthAccessTokenSecret("veodlw1zZkz1H4dikWRC7a2jyqAj87KahDcg7cGGZNjUd");
            TwitterFactory twitterFactory = new TwitterFactory(cb.build());
            twitter = twitterFactory.getInstance();
            instance = new TwitterLoader();
            Thread twitterThread = new Thread(instance);
            twitterThread.start();
            MainScreenContextListener.addThread(twitterThread);
        }
    }

    private TwitterLoader() {
        try {
            Properties properties = Config.loadProperties("mainscreen");
            mainHashTag = properties.getProperty("twitter.hashtag");
            pollHashTag = properties.getProperty("poll.hashtag", mainHashTag);
        } catch (IOException e) {
            logger.error("error", e);
        }
    }

    public synchronized void postMessage(String text) {
        try {
            twitter.updateStatus(text);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public void doOnStatus(Status status) {
        System.err.println(status.getUser().getId() + " " + status.getText());
        if (Arrays.stream(status.getHashtagEntities()).anyMatch(e -> ("#" + e.getText()).equals(mainHashTag))) {
            WordStatisticService.vote(WordStatisticService.TWEET_KEYWORD + " " + status.getText());
            MessageService.processTwitterMessage(status);
        }

        if (status.getText().startsWith(pollHashTag + " ")) {
            String text = status.getText().substring(pollHashTag.length() + 1);
            if (text.startsWith("vote")) {
                PollsService.vote("Twitter:" + status.getUser().getId(), text);
            }
        }
    }

    public void run() {
        while (true) {
            try {
                twitterStream = new TwitterStreamFactory().getInstance();
                StatusListener statusListener = new StatusListener() {
                    @Override
                    public void onStatus(Status status) {
                        doOnStatus(status);
                    }

                    @Override
                    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

                    }

                    @Override
                    public void onTrackLimitationNotice(int i) {

                    }

                    @Override
                    public void onScrubGeo(long l, long l2) {

                    }

                    @Override
                    public void onStallWarning(StallWarning stallWarning) {

                    }

                    @Override
                    public void onException(Exception e) {

                    }
                };
                FilterQuery filterQuery = new FilterQuery();
                filterQuery.track(mainHashTag, pollHashTag);

                twitterStream.addListener(statusListener);
                twitterStream.filter(filterQuery);

                break;
            } catch (Exception e) {
                logger.error("error", e);
            }
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        twitterStream.cleanUp();
        twitterStream.clearListeners();
    }

    public List<Status> loadByQuery(String query) throws TwitterException {
        List<Status> statuses;
        if (query.startsWith("@")) {
            String username = query.substring(1);
            statuses = twitter.getUserTimeline(username);
        } else {
            statuses = twitter.search(new Query(query)).getTweets();
        }
        statuses = statuses.subList(0, Math.min(statuses.size(), 7));
        Collections.reverse(statuses);
        return statuses;
    }
}
