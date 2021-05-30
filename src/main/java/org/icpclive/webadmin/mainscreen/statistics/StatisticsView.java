package org.icpclive.webadmin.mainscreen.statistics;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.icpclive.webadmin.utils.RefreshableContent;

public class StatisticsView extends HorizontalLayout  implements RefreshableContent<HorizontalLayout> {
    private final WordStatisticsController wordStatisticsController;
    private final FrameRateDemonstration frameRateDemonstration;
    private final StatisticDemonstration statisticDemonstration;
    public StatisticsView() {
        wordStatisticsController = new WordStatisticsController();
        frameRateDemonstration = new FrameRateDemonstration();
        statisticDemonstration =  new StatisticDemonstration();
        VerticalLayout demonstration = new VerticalLayout(frameRateDemonstration, statisticDemonstration);
        add(wordStatisticsController, demonstration);
    }

    @Override
    public HorizontalLayout getContent() {
        return this;
    }

    @Override
    public void refresh() {
        wordStatisticsController.refresh();
        frameRateDemonstration.refresh();
        statisticDemonstration.refresh();
    }
}
