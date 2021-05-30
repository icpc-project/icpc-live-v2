package org.icpclive.webadmin.mainscreen.caption;

import org.icpclive.webadmin.creepingline.MessageService;
import org.icpclive.webadmin.utils.Controller;

import java.util.ArrayList;
import java.util.List;

public class CreepingLineLogosController extends Controller<Advertisement> {

    public CreepingLineLogosController() {
        super(new String[]{"Logo"}, Advertisement.class,1, MessageService.getInstance().getLogosBackup());

    }

    @Override
    protected void recache() {
        MessageService.getInstance().recache();
    }

    @Override
    protected void setItemValue(final Advertisement item, final List<String> values) {
        item.setAdvertisement(values.get(0));
    }

    @Override
    protected void removeItem(final Advertisement item) {
        MessageService.getInstance().removeLogo(item);
    }

    @Override
    protected void addItem(final List<String> values) {
        MessageService.getInstance().addLogo(new Advertisement(values.get(0)));
    }

    @Override
    protected List<String> getTextFieldValuesFromItem(final Advertisement item) {
        List<String> field = new ArrayList<>();
        field.add(item.getAdvertisement());
        return field;
    }
}
