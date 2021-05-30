package org.icpclive.webadmin.mainscreen.standings;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import org.icpclive.datapassing.StandingsData;
import org.icpclive.events.ContestInfo;
import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.utils.Refreshable;

import java.util.ArrayList;
import java.util.List;

public class Standings extends VerticalLayout implements Refreshable {
    private final Label status;
    private final RadioButtonGroup<String> regions;
    private final RadioButtonGroup<StandingsData.OptimismLevel> optimismLevel;

    public Standings() {
        StandingsData data = MainScreenService.getInstance().getStandingsData();
        status = new Label(getStatus());
        status.getElement().getStyle().set("font-size", "large");
        final Button showFirstTop = newControllerButton("Show first page", true, StandingsData.StandingsType.ONE_PAGE, false);
        final Button showSecondTop = newControllerButton("Show two pages", true, StandingsData.StandingsType.TWO_PAGES, false);
        final Button showAll = newControllerButton("Show all pages", true, StandingsData.StandingsType.ALL_PAGES, false);
        final Button hide = newControllerButton("Hide", false, StandingsData.StandingsType.HIDE, false);
        final Button showFirstTopBig = newControllerButton("Show first page", true, StandingsData.StandingsType.ONE_PAGE, true);
        final Button showSecondTopBig = newControllerButton("Show two pages", true, StandingsData.StandingsType.TWO_PAGES, true);
        final Button showAllBig = newControllerButton("Show all pages", true, StandingsData.StandingsType.ALL_PAGES, true);
        FormLayout buttons = new FormLayout(showFirstTopBig, showSecondTopBig, showAllBig, hide);
        buttons.setWidth("50%");
        buttons.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));


        regions = new RadioButtonGroup<>();
        List<String> regionItems = new ArrayList<>();
        regionItems.add(StandingsData.ALL_REGIONS);
        regionItems.addAll(ContestInfo.GROUPS);
        regions.setItems(regionItems);
        regions.setValue(StandingsData.ALL_REGIONS);
        regions.addValueChangeListener(event -> {
           if (data.isVisible) {
               Notification.show("To update the standings should be hide");
               return;
           }
           if (!data.isBig()) {
               Notification.show("Compact standings could not have region filter");
           }
        });

        optimismLevel =  new RadioButtonGroup<>();
        optimismLevel.setItems(StandingsData.OptimismLevel.values());
        optimismLevel.setValue(StandingsData.OptimismLevel.NORMAL);
        optimismLevel.addValueChangeListener(event -> {
            String outcome = data.setStandingsVisible(data.isVisible, data.standingsType, data.isBig, data.region,
                    StandingsData.OptimismLevel.valueOf(optimismLevel.getValue().toString().toUpperCase()));
            if (outcome != null) {
                Notification.show(outcome);
            }
            status.setText(getStatus());
        });
        add(status,regions, optimismLevel, buttons);
    }
    public String getStatus() {
        return MainScreenService.getInstance().getStandingsData().toString();
    }

    private Button newControllerButton(String name, boolean visible, StandingsData.StandingsType type, boolean big) {
        StandingsData data = MainScreenService.getInstance().getStandingsData();
        Button controller = new Button(name);
        controller.addClickListener(event -> {
            if (visible && data.isStandingsVisible()) {
                Notification.show("You should hide standings first");
                return;
            }
            String selectedRegion = regions.getValue();
            String selectedOptimismLevel = optimismLevel.getValue().toString();

            String outcome = data.setStandingsVisible(visible, type, big, selectedRegion,
                    StandingsData.OptimismLevel.valueOf(selectedOptimismLevel.toUpperCase()));
            if (outcome != null) {
                Notification.show(outcome);
                return;
            }
            status.setText(getStatus());
        });
        return controller;
    }
    public void refresh() {
        status.setText(getStatus());
    }
}
