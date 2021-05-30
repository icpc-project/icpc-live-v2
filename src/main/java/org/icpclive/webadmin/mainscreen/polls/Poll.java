package org.icpclive.webadmin.mainscreen.polls;

import com.google.gson.*;
import org.icpclive.events.ContestInfo;
import org.icpclive.events.EventsLoader;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Poll {

    public static class Option implements Comparable<Option> {
        private int id;
        private String option;
        private int votes;

        public Option(final int id, final String option, final int votes) {
            this.id = id;
            this.option = option;
            this.votes = votes;
        }

        public int compareTo(final Option other) {
            return votes == other.votes ? this.option.compareTo(other.option) : votes - other.votes;
        }

        public int hashCode() {
            return option.hashCode();
        }

        public int getVotes() {
            return votes;
        }

        public void setVotes(int newVotes) {
            votes = newVotes;
        }

        public int getId() {
            return id;
        }

        public void setOption(String option) {
            this.option = option;
        }

        public void setId(final int id) {
            this.id = id;
        }

        public String getOption() {
            return option;
        }
    }

    private String question;
    private String hashTag;

    private Option[] options;
    private Set<String> votedUsers;

    private boolean teamOptions;

    private int totalOptions;
    public Poll(final String question, final String hashTag, final boolean teamOptions) {
        this.question = question;
        this.hashTag = hashTag;
        this.totalOptions = 0;
        this.teamOptions = teamOptions;
        this.votedUsers = new HashSet<>();
        if (teamOptions) {
            this.options = loadOptions(loadContestInfo());
        } else {
            this.options = new Option[0];
        }
    }

    public Poll(final String question, final String hashTag, final String[] stringOptions) {
        this.question = question;
        this.hashTag = hashTag;
        this.options = new Option[stringOptions.length];
        this.totalOptions = 0;
        addGridChar(stringOptions);
        for (int i = 0; i < options.length; i++) {
            this.options[i] = new Option(totalOptions++, stringOptions[i].toLowerCase(), 0);
        }
    }

    private ContestInfo loadContestInfo() {
        ContestInfo contestInfo;
        do {
            contestInfo = EventsLoader.getInstance().getContestData();
        } while (contestInfo == null);
        return contestInfo;
    }

    private Option[] loadOptions(final ContestInfo contestInfo) {
        String[] hashTags = contestInfo.getHashTags();
        Arrays.sort(hashTags);
        Option[] options = new Option[hashTags.length];
        for (int i = 0; i < options.length; i++) {
            if (hashTags[i] != null) {
                options[i] = new Option(totalOptions++, hashTags[i], 0);
            }
        }
        return options;


    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }


    public String getHashtag() {
        return hashTag;
    }

    public void setHashtag(String hashTag) {
        this.hashTag = hashTag;
    }

    public void setHashTag(final String hashTag) {
        this.hashTag = hashTag.toLowerCase();
    }

    public Option[] getOptions() {
        return options;
    }

    public void setOptions(final String[] hashTags) {
        addGridChar(hashTags);
        for (int i = 0; i < hashTags.length; i++) {
            hashTags[i] = hashTags[i].toLowerCase();
        }
        Option[] newOptions = new Option[hashTags.length];
        for (int i = 0; i < newOptions.length; i++) {
            int j = findIndexOfEqualOption(hashTags[i]);
            newOptions[i] = (j != -1) ? options[j] : new Option(i, hashTags[i], 0);
        }
        for (int i = 0; i < newOptions.length; i++) {
            newOptions[i].setId(i);
        }
        options = newOptions;
        totalOptions = newOptions.length;

    }

    private int findIndexOfEqualOption(final String hashTag) {
        int i = -1;
        for (int j = 0; j < options.length; j++) {
            if (options[j].getOption().equals(hashTag)) {
                i = j;
                break;
            }
        }
        return i;
    }

    private void addGridChar(final String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            if (!strings[i].startsWith("#")) {
                strings[i] = "#" + strings[i];
            }

        }
    }

    public boolean updateIfOption(final String user, String stringOption) {
        synchronized (this) {
            stringOption = stringOption.toLowerCase();
            if (votedUsers.contains(user)) {
                return true;
            }
            Option option = null;
            for (final Option curOption : options) {
                if (curOption.getOption().equalsIgnoreCase(stringOption)) {
                    option = curOption;
                    break;
                }
            }
            if (option != null) {
                option.setVotes(option.getVotes() + 1);
                votedUsers.add(user);
            }
            return option != null;
        }
    }

    public boolean getTeamOptions() {
        return teamOptions;
    }

    public void setTeamOptions(final boolean teamOptions) {
        this.teamOptions = teamOptions;
    }

    public Option[] getData() {
        synchronized (this) {
            return Arrays.copyOf(options, options.length);
        }
    }

    public String toString() {
        return "Poll: " + question + " " + hashTag + " " + Arrays.toString(options);
    }
    public static class PollSerializer implements JsonSerializer<Poll> {

        @Override
        public JsonElement serialize(final Poll poll, final Type type, final JsonSerializationContext jsonSerializationContext) {
            if (poll == null) {
                return null;
            }
            synchronized (poll) {
                final JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("question", poll.getQuestion());
                jsonObject.addProperty("hashtag", poll.getHashtag());
                jsonObject.addProperty("teamOptions", poll.getTeamOptions());
                JsonArray options = new JsonArray();
                for (final Option option : poll.options) {
                    JsonObject jsonOption = new JsonObject();
                    jsonOption.addProperty("id", option.getId());
                    jsonOption.addProperty("option", option.getOption());
                    jsonOption.addProperty("votes", option.getVotes());
                    options.add(jsonOption);
                }
                jsonObject.add("options", options);
                return jsonObject;
            }
        }
    }

    public static class PollDeserializer implements JsonDeserializer<Poll> {
        @Override
        public Poll deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String question = jsonObject.get("question").getAsString();
            String hashTag = jsonObject.get("hashtag").getAsString();
            boolean teamOptions = jsonObject.get("teamOptions").getAsBoolean();

            JsonArray jsonOptions = jsonObject.get("options").getAsJsonArray();
            Option[] options = new Option[jsonOptions.size()];
            for (int i = 0; i < options.length; i++) {
                JsonObject jo = jsonOptions.get(i).getAsJsonObject();
                options[i] = new Option(
                        jo.get("id").getAsInt(),
                        jo.get("option").getAsString(),
                        jo.get("votes").getAsInt()
                );
            }
            Poll poll = new Poll(question, hashTag, teamOptions);
            poll.options = options;
            return poll;
        }
    }
}
