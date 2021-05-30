package org.icpclive.webadmin.mainscreen.standings;

import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.utils.HideShowDemonstration;

public class ClockDemonstration extends HideShowDemonstration {

    public ClockDemonstration() {
        super("Clock");
    }

    @Override
    protected boolean getVisibleStatus() {
        return MainScreenService.getInstance().getClockData().isClockVisible();
    }

    @Override
    protected void setVisibility(final boolean visibility) {
        MainScreenService.getInstance().getClockData().setClockVisible(visibility);
    }
}
