package org.icpclive.webadmin.mainscreen.caption;

import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.utils.DemonstratorController;

import java.util.Collections;
import java.util.List;

public class AdvertisementController extends DemonstratorController<Advertisement> {

    public AdvertisementController() {
        super(new String[]{"advertisement"}, new String[]{"Advertisement"}, Advertisement.class, 1,
                MainScreenService.getInstance().getAdvertisementData().getContainer());
        deleteDemonstrateAllButtons();
    }

    @Override
    protected void recache() {
        //
    }

    @Override
    protected void setItemValue(final Advertisement item, final List<String> value) {
        item.setAdvertisement(value.get(0));
    }

    @Override
    protected void removeItem(final Advertisement item) {
        MainScreenService.getInstance().getAdvertisementData().removeAdvertisement(item);
    }

    @Override
    protected void addItem(final List<String> fields) {
        MainScreenService.getInstance().getAdvertisementData().addAdvertisement(new Advertisement(fields.get(0)));
    }

    @Override
    protected List<String> getTextFieldValuesFromItem(final Advertisement item) {
        return Collections.singletonList(item.getAdvertisement());
    }


    @Override
    protected String getStatus(final int id) {
        return MainScreenService.getInstance().getAdvertisementData().toString();
    }

    @Override
    protected String setVisible(final boolean visible, final Advertisement item, int id) {
        return MainScreenService.getInstance().getAdvertisementData().setAdvertisementVisible(visible, item);
    }
}
