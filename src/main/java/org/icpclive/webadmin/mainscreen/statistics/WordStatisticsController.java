package org.icpclive.webadmin.mainscreen.statistics;


import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.utils.DemonstratorController;

import java.util.ArrayList;
import java.util.List;

public class WordStatisticsController extends DemonstratorController<WordStatistics> {

    public WordStatisticsController() {
        super(new String[] {"word"}, new String[] {"Name", "Text", "Picture"}, WordStatistics.class, 1, WordStatisticService.getInstance().getContainer());
        deleteDemonstrateAllButtons();
        deleteHideButtons();
    }


    @Override
    protected void recache() {
        //
    }

    @Override
    protected void setItemValue(final WordStatistics item, final List<String> values) {
        item.setWordName(values.get(0));
        item.setWord(values.get(1));
        item.setPicture(values.get(2));
    }

    @Override
    protected void removeItem(final WordStatistics item) {
        WordStatisticService.getInstance().removeWord(item);
    }

    @Override
    protected void addItem(final List<String> values) {
        WordStatistics item = new WordStatistics(values.get(0), values.get(1), values.get(2));
        WordStatisticService.getInstance().addWord(item);
    }

    @Override
    protected List<String> getTextFieldValuesFromItem(final WordStatistics item) {
        List<String> fields = new ArrayList<>();
        fields.add(item.getWordName());
        fields.add(item.getWord());
        fields.add(item.getPicture());
        return fields;
    }

    @Override
    protected String getStatus(final int id) {
        return MainScreenService.getInstance().getWordStatisticsData().toString();
    }

    @Override
    protected String setVisible(final boolean visible, final WordStatistics item, final int id) {
        return MainScreenService.getInstance().getWordStatisticsData().setWordVisible(item);
    }
}
