package org.icpclive.webadmin.creepingline;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.icpclive.Config;
import org.icpclive.backend.Preparation;
import org.icpclive.datapassing.CreepingLineData;
import org.icpclive.datapassing.Data;
import org.icpclive.events.EventsLoader;
import org.icpclive.events.WF.WFAnalystMessage;
import org.icpclive.events.WF.json.WFEventsLoader;
import org.icpclive.webadmin.MainScreenContextListener;
import org.icpclive.webadmin.backup.BackUp;
import org.icpclive.webadmin.mainscreen.caption.Advertisement;
import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.utils.LoopThread;
import twitter4j.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class MessageService {
    private static final Logger log = LogManager.getLogger(MessageService.class);
    private static MessageService instance;
    private static final Set<String> existingMessages = new HashSet<>();
    private final static List<Message> messageFlow = new CopyOnWriteArrayList<>();
    private String backUp;
    private String logoBackUp;
    private final BackUp<Message> messageBackup;
    private final BackUp<Advertisement> logosBackup;
    private boolean isVisible;

    public static MessageService getInstance() {
        if (instance == null) {
            instance = new MessageService();
        }
        return instance;
    }

    private MessageService() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/creepingline.properties"));
            this.backUp = properties.getProperty("backup.file.name");
            this.logoBackUp = properties.getProperty("backup.logo.file.name");
        } catch (IOException e) {
            log.error("Error during reading creepingline properties", e);
        }
        this.messageBackup = new BackUp<>(backUp, Message.class);
        this.logosBackup = new BackUp<>(logoBackUp, Advertisement.class);
        this.isVisible = true;
        Thread messageUpdater = newMessageFlowUpdater();
        messageUpdater.start();
        MainScreenContextListener.addThread(messageUpdater);
        Thread analyticsThread = newAnalyticsThread();
        analyticsThread.start();
        MainScreenContextListener.addThread(analyticsThread);

    }

    private LoopThread newMessageFlowUpdater() {
        return new LoopThread(
                () -> {
                    List<Message> messagesToDelete = new ArrayList<>();
                    messageBackup.getData().forEach(message -> {
                        if (message.getEndTime() < System.currentTimeMillis()) {
                            messagesToDelete.add(message);
                        }
                    });
                    messagesToDelete.forEach(this::removeMessage);
                    messagesToDelete.clear();
                    int maxFlow = MainScreenService.getProperties().getCreepingLineProperties().getMaximumFlowSize();
                    for (int i = maxFlow; i < messageFlow.size(); i++) {
                        messagesToDelete.add(messageFlow.get(i));
                    }
                    messagesToDelete.forEach(messageFlow::remove);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        log.error("Message updater thread was interrupted", e);
                    }
                }
        );
    }


    private LoopThread newAnalyticsThread() {
        return new LoopThread(() -> {
            Properties properties = new Properties();
            EventsLoader eventsLoader = EventsLoader.getInstance();
            while (true) {
                try {
                    properties = Config.loadProperties("events");
                } catch (IOException e) {
                    log.error("Events properties cannot be loaded", e);
                    sleep();
                }
                if (!(eventsLoader instanceof WFEventsLoader)) {
                    return;
                }
                WFEventsLoader wfEventsLoader = (WFEventsLoader) eventsLoader;
                while (eventsLoader.getContestData() == null) ;
                while (eventsLoader.getContestData().getStartTime() == 0) ;
                String url = properties.getProperty("analytics.url", null);
                if (url == null) {
                    log.info("There is no analytics feed");
                    return;
                }
                String login = properties.getProperty("analytics.login", "");
                String password = properties.getProperty("analytics.password", "");
                readAnalyticsFeed(url, login, password, wfEventsLoader);
            }
        });
    }

    private void readAnalyticsFeed(final String url, final String login, final String password, final WFEventsLoader eventsLoader) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Preparation.openAuthorizedStream(url, login, password)))) {
            String line;
            while ((line = br.readLine()) != null) {
                JsonObject je = new Gson().fromJson(line, JsonObject.class);
                if ("commentary-messages".equals(je.get("type").getAsString())) {
                    WFAnalystMessage message = eventsLoader.readAnalystMessage(je.get("data").getAsJsonObject());
                    long endTime = message.getTime() + MainScreenService.getProperties()
                            .getCreepingLineProperties()
                            .getMessageLifespanCreepingLine();
                    if (message.getPriority() <= 3 && endTime > System.currentTimeMillis()) {
                        addMessageToFlow(new Message(message.getMessage(), message.getTime(), MainScreenService.getProperties()
                                .getCreepingLineProperties().getMessageLifespanCreepingLine(), false, "Analytics"));

                    }
                }
            }
        } catch (IOException e) {
            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.error("Thread was interrupted", e);
        }
    }
    public static void processTwitterMessage(Status status) {
        Message message = new Message(
                status.getText(),
                System.currentTimeMillis(),
                MainScreenService.getProperties().getCreepingLineProperties().getMessageLifespanCreepingLine(),
                false,
                "@" + status.getUser().getScreenName());
        addMessageToFlow(message);
    }
    private static void addMessageToFlow(Message message) {
        if (!existingMessages.contains(message.getMessage())) {
            messageFlow.add(0, message);
            existingMessages.add(message.getMessage());
        }
    }
    public void addMessage(Message message) {
        messageBackup.addItem(message);
    }


    public void removeMessage(final Message message) {
        messageBackup.removeItem(message);
        recache();
    }
    public void setMessageValues(Message message, String messageText, boolean advertisement) {
        synchronized (messageBackup.getData()) {
            message.setMessage(messageText);
            message.setAdvertisement(advertisement);
        }
    }
    public List<Message> getMessageContainer() {
        return messageBackup.getData();
    }
    public List<Message> getMessageFlowContainer() {
        return messageFlow;
    }

    public synchronized void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
        recache();
    }

    public synchronized boolean isVisible() {
        return isVisible;
    }
    public void recache() {
        synchronized (messageBackup) {
            synchronized (logosBackup) {
                Data.cache.refresh(CreepingLineData.class);
            }
        }
    }
    public void addLogo(Advertisement logo) {
        logosBackup.addItem(logo);
    }

    public void removeLogo(Advertisement logo) {
        logosBackup.removeItem(logo);
    }

    public List<Advertisement> getLogosBackup() {
        return logosBackup.getData();
    }


}
