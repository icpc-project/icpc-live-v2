package org.icpclive.webadmin.locator;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import org.icpclive.backend.player.widgets.locator.LocatorCamera;
import org.icpclive.backend.player.widgets.locator.LocatorsData;
import org.icpclive.datapassing.LocatorData;
import org.icpclive.events.TeamInfo;
import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.mainscreen.properties.TeamProperties;
import org.icpclive.webadmin.utils.RefreshableContent;
import org.icpclive.webadmin.utils.Utils;

import java.util.Set;

public class LocatorView extends VerticalLayout  implements RefreshableContent<VerticalLayout> {
    private final LocatorData data;
    private final Label status;
    private final Button showButton;
    private final Button hideButton;
    private final Button clearButton;
    private final Label teamSelectedStatus;
    private final CheckboxGroup<TeamInfo> teamSelection;
    private final RadioButtonGroup<LocatorCamera> locatorSelection;


    public LocatorView() {
        data = MainScreenService.getInstance().getLocatorData();

        status = new Label(getStatus());
        teamSelection = newTeamSelection();

        locatorSelection = new RadioButtonGroup<>();
        locatorSelection.addValueChangeListener(event -> {
            data.setCameraID(event.getValue().cameraID);
        });
    //    locatorSelection.setItems(LocatorsData.locatorCameras);
     //   locatorSelection.setValue(LocatorsData.locatorCameras.get(0));

        showButton = new Button("Show info");
        showButton.addClickListener(event -> {
            String outcome = data.setVisible();
            if (outcome != null) {
                Notification.show(outcome);
            }
        });


        hideButton = new Button("Stop");
        hideButton.addClickListener(event -> {
            data.hide();
            teamSelection.clear();
        });

        clearButton = new Button("Clear");
        clearButton.addClickListener(event -> teamSelection.clear());

        teamSelectedStatus = new Label("Nothing selected");

        add(status);
        setHorizontalComponentAlignment(Alignment.CENTER, status);

        FormLayout controller = new FormLayout(showButton, hideButton, clearButton);
        controller.setWidth("50%");
        controller.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 3));
        add(controller);
        setHorizontalComponentAlignment(Alignment.CENTER, controller);

        add(teamSelectedStatus);
        setHorizontalComponentAlignment(Alignment.CENTER, teamSelectedStatus);


        add(teamSelection);
        setHorizontalComponentAlignment(Alignment.CENTER, teamSelection);


    }


    private CheckboxGroup<TeamInfo> newTeamSelection() {
        CheckboxGroup<TeamInfo> teamSelection = new CheckboxGroup<>();
        TeamProperties properties = MainScreenService.getProperties().getTeamProperties();
        teamSelection.setItems(properties.getTeamInfos());
        teamSelection.addValueChangeListener(event -> {
            Set<TeamInfo> selection = teamSelection.getSelectedItems();
            data.setTeams(selection);
            StringBuilder stringBuilderSelectedTeams = new StringBuilder();
            for (TeamInfo teamInfo : data.getTeams()) {
                stringBuilderSelectedTeams.append(teamInfo.getShortName()).append(", ");
            }
            String selectedTeams = stringBuilderSelectedTeams.toString();
            if (selectedTeams.isEmpty()) {
                selectedTeams = "Nothing is selected, ";
            }
            teamSelectedStatus.setText(selectedTeams.substring(0, selectedTeams.length() - 2));
        });
        teamSelection.getChildren().forEach(item -> item.getElement().getStyle().set("width", Utils.TEAM_FIELD_WIDTH));
        return teamSelection;
    }


    public String getStatus() {
        return data.getStatus();
    }

    @Override
    public VerticalLayout getContent() {
        return this;
    }

    @Override
    public void refresh() {
        status.setText(getStatus());
    }


}
