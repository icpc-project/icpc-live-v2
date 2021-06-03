package org.icpclive.webadmin.backup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.icpclive.datapassing.TeamData;
import org.icpclive.webadmin.MainScreenContextListener;
import org.icpclive.webadmin.mainscreen.polls.Poll;
import org.icpclive.webadmin.utils.LoopThread;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BackUp<T> {
    private static final Logger log = LogManager.getLogger(BackUp.class);
    private final String backupFileName;
    private final Path backupFile;
    private final List<T> data;
    private final Class<T> type;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(TeamData.class, new TeamData.TeamDataDeserializer())
            .registerTypeAdapter(TeamData.class, new TeamData.TeamDataSerializer())
            .registerTypeAdapter(Poll.class, new Poll.PollSerializer())
            .registerTypeAdapter(Poll.class, new Poll.PollDeserializer())
            .create();

    public BackUp(final String backupFileName, final Class<T> type) {
        this.backupFileName = backupFileName;
        this.backupFile = Paths.get("backup", backupFileName);
        this.data = Collections.synchronizedList(new ArrayList<>());
        this.type = type;
        reload();
        LoopThread schedule = new LoopThread(() -> {
           backup();
           try {
               Thread.sleep(60000L);
           } catch (InterruptedException e) {
               log.error("Backup thread was interrupted");
           }
        });
        schedule.start();
        MainScreenContextListener.addThread(schedule);
    }

    public void reload() {
        synchronized (data) {
            data.clear();
            if (Files.exists(backupFile)) {
                try {
                    Files.readAllLines(backupFile).forEach(line -> data.add(gson.fromJson(line, type)));
                } catch (IOException e) {
                    log.error("Error while reloading backup", e);
                }
            }
        }
    }

    public void backup() {
        Path tempFile = Paths.get("tmp", backupFileName + ".tmp");
        try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
            synchronized (data) {
                for (T item : data) {
                    writer.write(gson.toJson(item));
                    writer.write(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            log.error("Error while making backup", e);
        }
        try {
            Files.move(tempFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Error during moving temp backup file to main backup file");
        }
    }

    public void addItem(T item) {
        synchronized (data) {
            data.add(item);
        }
    }

    public void addItemAt(int index, T item) {
        synchronized (data) {
            data.add(index, item);
        }
    }

    public void removeItem(T item) {
        synchronized (data) {
            data.remove(item);
        }
    }

    public List<T> getData() {
        return data;
    }
}
