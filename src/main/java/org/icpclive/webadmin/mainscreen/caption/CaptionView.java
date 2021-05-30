package org.icpclive.webadmin.mainscreen.caption;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.icpclive.webadmin.utils.RefreshableContent;

public class CaptionView extends HorizontalLayout implements RefreshableContent<HorizontalLayout> {
    private final AdvertisementController advertisementController;
    private final PersonController personController;

    public CaptionView() {
        setSizeFull();
        advertisementController =  new AdvertisementController();


        personController = new PersonController();
        personController.setSizeFull();
        VerticalLayout advertisementLogosLayout = new VerticalLayout(advertisementController, new CreepingLineLogosController());
        advertisementLogosLayout.setSizeFull();


        add(advertisementLogosLayout);
        add(advertisementLogosLayout, personController);
    }

    public void refresh() {
        advertisementController.refresh();
        personController.refresh();
    }

    @Override
    public HorizontalLayout getContent() {
        return this;
    }
}
