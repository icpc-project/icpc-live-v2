package org.icpclive.webadmin.mainscreen.statistics;

public class WordStatistics {
    private String word;
    private String wordName;
    private String picture;
    private long count;

    public WordStatistics(final String wordName, final String word, final String picture) {
        this.word = word;
        this.wordName = wordName;
        this.picture = picture;
        this.count = 0;
    }


    public String getWord() {
        return word;
    }

    public String getWordName() {
        return wordName;
    }

    public String getPicture() {
        return picture;
    }

    public long getCount() {
        return count;
    }

    public void setWord(final String word) {
        this.word = word;
    }

    public void setWordName(final String wordName) {
        this.wordName = wordName;
    }

    public void setPicture(final String picture) {
        this.picture = picture;
    }

    public void setCount(final long count) {
        this.count = count;
    }


    public String toString() {
        return word + " " + picture + " " + count;
    }

}
