package org.icpclive.webadmin.mainscreen.statistics;

import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.utils.HideShowDemonstration;

public class StatisticDemonstration extends HideShowDemonstration {
    public StatisticDemonstration() {
        super("Statistics");
    }

    @Override
    protected boolean getVisibleStatus() {
        return MainScreenService.getInstance().getStatisticsData().isVisible();
    }

    @Override
    protected void setVisibility(final boolean visibility) {
        MainScreenService.getInstance().getStatisticsData().setVisible(visibility);
    }
}
