package org.icpclive.webadmin.creepingline;

import org.icpclive.webadmin.utils.HideShowDemonstration;

public class CreepingLineDemonstrator extends HideShowDemonstration {
    public CreepingLineDemonstrator() {
        super("Creeping line");
    }

    @Override
    protected boolean getVisibleStatus() {
        return MessageService.getInstance().isVisible();
    }

    @Override
    protected void setVisibility(final boolean visibility) {
        MessageService.getInstance().setVisible(visibility);
    }
}
