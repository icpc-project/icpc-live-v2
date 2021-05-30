package org.icpclive.webadmin.mainscreen.statistics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.icpclive.datapassing.WordStatisticsData;
import org.icpclive.webadmin.backup.BackUp;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class WordStatisticService {
    public final static String TWEET_KEYWORD = "$tweet$";
    private static final Logger log = LogManager.getLogger(WordStatisticsData.class);
    private static WordStatisticService instance;
    private static BackUp<WordStatistics> wordsBackup;
    private String backUpFile;

    public static WordStatisticService getInstance() {
        if (instance == null) {
            instance = new WordStatisticService();
        }
        return instance;
    }
    private WordStatisticService() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/mainscreen.properties"));
            backUpFile = properties.getProperty("word.statistics.backup.file");
        } catch (IOException e) {
            log.error("Error during reading word statistics property file", e);
        }
        wordsBackup = new BackUp<>(backUpFile, WordStatistics.class);
        fillWords(properties);
    }

    private void fillWords(Properties properties) {
        if (wordsBackup.getData().size() != 0) {
            return;
        }
        String[] words = properties.getProperty("word.statistics.words").split(";");
        for (String word : words) {
            String picture = properties.getProperty("word.statistics." + word + ".picture");
            String realWord = properties.getProperty("word.statistics." + word + ".text");
            wordsBackup.addItem(new WordStatistics(word, realWord, picture));
        }
    }

    public void addWord(WordStatistics word) {
        wordsBackup.addItem(word);
    }
    public void removeWord(WordStatistics word) {
        wordsBackup.removeItem(word);
    }

    public List<WordStatistics> getContainer() {
        return wordsBackup.getData();
    }

    public static void vote(String text) {
        if (wordsBackup == null) {
            return;
        }
        text = text.toLowerCase();
        for (WordStatistics word : wordsBackup.getData()) {
            String[] patterns = word.getWord().toLowerCase().split(";");
            for (String pattern : patterns) {
                if (text.contains(pattern)) {
                    word.setCount(word.getCount() + 1);
                    break;
                }
            }
        }
    }



}
