package org.icpclive.webadmin.mainscreen.standings;

import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.utils.HideShowDemonstration;

public class QueueDemonstration extends HideShowDemonstration {

    public QueueDemonstration() {
        super("Queue");
    }

    @Override
    protected boolean getVisibleStatus() {
        return MainScreenService.getInstance().getQueueData().isQueueVisible();
    }

    @Override
    protected void setVisibility(final boolean visibility) {
        MainScreenService.getInstance().getQueueData().setVisible(visibility);
    }
}
