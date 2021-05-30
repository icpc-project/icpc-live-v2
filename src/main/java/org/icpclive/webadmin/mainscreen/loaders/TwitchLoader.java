package org.icpclive.webadmin.mainscreen.loaders;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.icpclive.Config;
import org.icpclive.datapassing.PollData;
import org.icpclive.webadmin.MainScreenContextListener;
import org.icpclive.webadmin.mainscreen.polls.PollsService;
import org.icpclive.webadmin.mainscreen.statistics.WordStatisticService;
import org.pircbotx.Configuration;
import org.pircbotx.MultiBotManager;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

public class TwitchLoader implements Runnable {
    private static final Logger logger = LogManager.getLogger(TwitchLoader.class);
    private  static TwitchLoader instance;
    private final MultiBotManager manager;

    public static TwitchLoader start() {
        if (instance == null) {
            instance = new TwitchLoader();
            Thread twitchThread = new Thread(instance);
            twitchThread.start();
            MainScreenContextListener.addThread(twitchThread);
        }
        return instance;
    }

    private TwitchLoader() {
        Properties properties;
        String url = null;
        String username = null;
        String password = null;
        String channels = null;
        try {
            properties = Config.loadProperties("mainscreen");
            url = properties.getProperty("twitch.chat.server", "irc.chat.twitch.tv");
            username = properties.getProperty("twitch.chat.username");
            password = properties.getProperty("twitch.chat.password");
            channels = properties.getProperty("twitch.chat.channel", "#" + username);
        } catch (IOException e){
            logger.error("Error during reading twitch properties", e);
        }
        Configuration.Builder configuration = new Configuration.Builder()
                .setAutoNickChange(false)
                .setOnJoinWhoEnabled(false)
                .setCapEnabled(true)
                .setName(username)
                .setServerPassword(password)
                .addServer(url)
                .addListener(new ListenerAdapter() {
                    AtomicLong lastTimeStamp = new AtomicLong();
                    @Override
                    public void onMessage(final MessageEvent event) throws Exception {
                        logger.log(Level.INFO, "Message: " + event.getUser() + " " + event.getMessage());
                        long previousTimeStamp = lastTimeStamp.get();
                        if (event.getTimestamp() > previousTimeStamp + 50) {
                            lastTimeStamp.weakCompareAndSet(previousTimeStamp, event.getTimestamp());
                            String message = event.getMessage();
                            if (message.startsWith("!vote")) {
                                PollsService.vote("Twitch:" + event.getUser().getLogin(), message.substring(1));
                            }
                            WordStatisticService.vote(message);
                        }
                    }
                }).setEncoding(StandardCharsets.UTF_8);
        manager = new MultiBotManager();
        for (String channel : channels.split(";")) {
            manager.addBot(configuration.addAutoJoinChannel(channel).buildConfiguration());
        }
    }

    @Override
    public void run() {
        manager.start();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        manager.stop();
    }
}
