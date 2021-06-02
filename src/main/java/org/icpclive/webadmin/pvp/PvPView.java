package org.icpclive.webadmin.pvp;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.icpclive.datapassing.PvPData;
import org.icpclive.events.TeamInfo;
import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.mainscreen.properties.TeamProperties;
import org.icpclive.webadmin.utils.RefreshableContent;
import org.icpclive.webadmin.utils.Utils;

import java.util.Set;

public class PvPView extends VerticalLayout implements RefreshableContent<VerticalLayout> {
    private final PvPData data;
    private final Label status;
    private final Button showButton;
    private final Button hideButton;
    private final Button clearButton;
    private final Label firstTeam;
    private final Label secondTeam;
    private final CheckboxGroup<TeamInfo> teamSelection;


    public PvPView() {
        data = MainScreenService.getInstance().getPvpData();
        
        status = new Label(getStatus());
        teamSelection = newTeamSelection();

        showButton = new Button("Show info");
        showButton.addClickListener(event -> {
            String outcome = data.setVisible();
            if (outcome != null) {
                Notification.show(outcome);
            }
        });

        hideButton = new Button("Stop");
        hideButton.addClickListener(event -> data.hide());
        
        clearButton = new Button("Clear");
        clearButton.addClickListener(event -> teamSelection.clear());

        firstTeam = new Label("First team");
        secondTeam = new Label("Second team");

        add(status);
        setHorizontalComponentAlignment(Alignment.CENTER, status);

        FormLayout controller = new FormLayout(showButton, hideButton, clearButton);
        controller.setWidth("50%");
        controller.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 3));
        add(controller);
        setHorizontalComponentAlignment(Alignment.CENTER, controller);

        HorizontalLayout teamStatuses = new HorizontalLayout(firstTeam, secondTeam);
        add(teamStatuses);
        setHorizontalComponentAlignment(Alignment.CENTER, teamStatuses);


        add(teamSelection);
        setHorizontalComponentAlignment(Alignment.CENTER, teamSelection);


    }

    private CheckboxGroup<TeamInfo> newTeamSelection() {
        CheckboxGroup<TeamInfo> teamSelection = new CheckboxGroup<>();
        TeamProperties properties = MainScreenService.getProperties().getTeamProperties();
        teamSelection.setItems(properties.getTeamInfos());
        teamSelection.addValueChangeListener(event -> {
            Set<TeamInfo> selection = teamSelection.getSelectedItems();
            Set<TeamInfo> left = data.setTeams(selection);
            if (selection.size() >= 3) {
                teamSelection.setValue(left);
                Notification.show("You can choose only 2 teams");
            }
            firstTeam.setText(data.getTeam(0));
            secondTeam.setText(data.getTeam(1));
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
        status.setText(data.getStatus());
    }
}
