package org.icpclive.webadmin.mainscreen.polls;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.icpclive.Config;
import org.icpclive.webadmin.backup.BackUp;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PollsService {
    private static final Logger log = LogManager.getLogger(PollsService.class);
    private static PollsService instance;
    private static final Map<String, Poll> pollByHashtag = new ConcurrentHashMap<>();
    private final BackUp<Poll> polls;

    public static PollsService getInstance() {
        if (instance == null) {
            instance = new PollsService();
        }
        return instance;
    }

    public PollsService() {
        Properties properties;
        String backUpFileName = "";
        try {
            properties = Config.loadProperties("mainscreen");
            backUpFileName = properties.getProperty("polls.backup.file");
        } catch (IOException e) {
            log.error("Error during loading mainscreen properties");
        }
        polls = new BackUp<>(backUpFileName, Poll.class);
        for (Poll poll : polls.getData()) {
            pollByHashtag.put(poll.getHashtag(), poll);
        }
    }
    public List<Poll> getPollsList() {
        return polls.getData();
    }

    public void addPoll(Poll poll) {
        polls.addItem(poll);
        pollByHashtag.put(poll.getHashtag().toLowerCase(), poll);
    }
    public void removePoll(Poll poll) {
        polls.removeItem(poll);
        pollByHashtag.remove(poll.getHashtag(), poll);
    }
    public void updateHashtag(Poll poll, String hashtag) {
        pollByHashtag.remove(poll.getHashtag());
        pollByHashtag.put(hashtag, poll);
    }
    public static void vote(String user, String message) {
        if (message.startsWith("vote ")) {
            message = message.substring(("vote ".length()));
        } else {
            return;
        }
        String[] tokens = message.toLowerCase().split(" ");
        if (tokens.length != 2) {
            return;
        }
        Poll pollToUpdate = pollByHashtag.get(tokens[0].startsWith("#") ? tokens[0] : "#" + tokens[0]);
        if (pollToUpdate == null) {
            return;
        }
        pollToUpdate.updateIfOption(user, tokens[1].startsWith("#") ? tokens[1] : "#" + tokens[1]);
    }
}
