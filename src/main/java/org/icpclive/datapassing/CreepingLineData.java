package org.icpclive.datapassing;

import org.icpclive.webadmin.creepingline.Message;
import org.icpclive.webadmin.creepingline.MessageService;
import org.icpclive.webadmin.mainscreen.caption.Advertisement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aksenov239 on 21.11.2015.
 */
public class CreepingLineData extends CachedData {
    public boolean isVisible;
    public List<Message> messages;
    public List<Advertisement> logos;

    public CreepingLineData() {
        isVisible = true;
        messages = new ArrayList<>();
        logos = new ArrayList<>();
    }

    public CreepingLineData initialize() {
        //messages = MessageService.getInstance().getMessages();
        logos = MessageService.getInstance().getLogosBackup();
        isVisible = MessageService.getInstance().isVisible();

        return this;
    }
}
