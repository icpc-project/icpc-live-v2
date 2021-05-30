package org.icpclive.datapassing;

import com.google.gson.*;
import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.mainscreen.polls.Poll;

import java.lang.reflect.Type;

/**
 * Created by Aksenov239 on 26.03.2017.
 */
public class PollData extends CachedData {
    @Override
    public PollData initialize() {
        PollData data = MainScreenService.getInstance().getPollData();
        this.poll = data.poll;
        this.isVisible = data.isVisible;
        this.timestamp = data.timestamp;
        this.delay = data.delay;

        return data;
    }

    public String getOverlayError() {
        return "You have to wait while poll information is shown";
    }

    public String checkOverlays() {
        MainScreenService mainScreenService = MainScreenService.getInstance();
        if (mainScreenService.getTeamData().isVisible) {
            return mainScreenService.getTeamData().getOverlayError();
        }
        if (mainScreenService.getPvpData().isVisible()) {
            return mainScreenService.getPvpData().getOverlayError();
        }
        return null;
    }

    public void switchOverlaysOff() {
        MainScreenService mainScreenService = MainScreenService.getInstance();
        boolean turnOff = false;
        if (mainScreenService.getStatisticsData().isVisible()) {
            mainScreenService.getStandingsData().hide();
            turnOff = true;
        }
        if (mainScreenService.getStatisticsData().isVisible()) {
            mainScreenService.getStatisticsData().hide();
            turnOff = true;
        }
        if (mainScreenService.getPictureData().isVisible()) {
            mainScreenService.getPictureData().hide();
            turnOff = true;
        }
        if (turnOff) {
            delay = MainScreenService.getProperties().getOverlayedDelay();
        } else {
            delay = 0;
        }
    }

    public void hide() {
        synchronized (this) {
            isVisible = false;
            recache();
        }
    }

    public String setPollVisible(Poll poll) {
        synchronized (this) {
            String error = checkOverlays();
            if (error != null) {
                return error;
            }
            if (isVisible) {
                return "Poll " + poll.getHashtag() + " is already shown now";
            }
            timestamp = System.currentTimeMillis();
            this.poll = poll;
//            System.err.println("Set poll " + this.poll.getHashtag());
            isVisible = true;
            switchOverlaysOff();
            recache();
        }
        return null;
    }

    public String toString() {
        return isVisible ? "Show poll " + poll.getHashtag() +
                " already for " +
                (System.currentTimeMillis() - timestamp) / 1000 +
                " seconds"
                : "No poll is shown";
    }

    public void recache() {
        Data.cache.refresh(PollData.class);
    }

    public Poll poll;
    public boolean isVisible = false;

    public static class PollDataSerializer implements JsonSerializer<PollData> {
        @Override
        public JsonElement serialize(PollData pollData, Type type, JsonSerializationContext jsonSerializationContext) {
            synchronized (pollData) {
                final JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("timestamp", pollData.timestamp);
                jsonObject.addProperty("isVisible", pollData.isVisible);
                jsonObject.addProperty("delay", pollData.delay);
                Poll poll = pollData.poll;
                Gson gsonSerializer = new GsonBuilder()
                        .registerTypeAdapter(Poll.class, new Poll.PollSerializer())
                        .create();
                jsonObject.addProperty("poll",
                        poll == null ? "null" : gsonSerializer.toJson(poll));
                return jsonObject;
            }
        }
    }

    public static class PollDataDeserializer implements JsonDeserializer<PollData> {
        @Override
        public PollData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            PollData pollData = new PollData();

            final JsonObject jsonObject = jsonElement.getAsJsonObject();

            String pollString = jsonObject.get("poll").getAsString();
            if (pollString.equals("null")) {
                pollData.poll = null;
            } else {
                Gson gsonDeserializer = new GsonBuilder()
                        .registerTypeAdapter(Poll.class, new Poll.PollDeserializer())
                        .create();
                pollData.poll = gsonDeserializer.fromJson(pollString, Poll.class);
            }

            pollData.timestamp = jsonObject.get("timestamp").getAsLong();
            pollData.isVisible = jsonObject.get("isVisible").getAsBoolean();
            pollData.delay = jsonObject.get("delay").getAsInt();
            return pollData;
        }
    }
}
