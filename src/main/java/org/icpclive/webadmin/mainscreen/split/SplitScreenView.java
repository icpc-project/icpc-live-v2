package org.icpclive.webadmin.mainscreen.split;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.icpclive.backend.SplitScreen;
import org.icpclive.webadmin.utils.RefreshableContent;

import java.util.ArrayList;
import java.util.List;

public class SplitScreenView extends HorizontalLayout implements RefreshableContent<HorizontalLayout> {
    private final static int CONTROLLER_COUNT = 4;
    private final List<Controller> controllers;

    public SplitScreenView() {
        controllers = new ArrayList<>();
        for (int i = 0; i < CONTROLLER_COUNT; i++) {
            controllers.add(new Controller(i));
        }
        add(newControllerVerticalLayout(controllers.get(0), controllers.get(1)),
                newControllerVerticalLayout(controllers.get(2), controllers.get(3)));
    }

    private VerticalLayout newControllerVerticalLayout(Controller left, Controller right) {
        return new VerticalLayout(left, right);
    }

    @Override
    public HorizontalLayout getContent() {
        return this;
    }

    @Override
    public void refresh() {
        for (int i = 0; i < CONTROLLER_COUNT; i++) {
            controllers.get(i).refresh();
        }
    }
}
