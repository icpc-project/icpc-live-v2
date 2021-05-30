package org.icpclive.webadmin.mainscreen.statistics;

import org.icpclive.datapassing.FrameRateData;
import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.utils.HideShowDemonstration;

public class FrameRateDemonstration extends HideShowDemonstration {
    public FrameRateDemonstration() {
        super("Frame rate");
    }

    @Override
    protected boolean getVisibleStatus() {
        return MainScreenService.getInstance().getFrameRateData().isVisible;
    }

    @Override
    protected void setVisibility(final boolean visibility) {
        MainScreenService.getInstance().getFrameRateData().setFrameRateVisible(visibility);
    }
}
