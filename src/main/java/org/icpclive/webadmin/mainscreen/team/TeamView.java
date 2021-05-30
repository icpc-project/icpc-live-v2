package org.icpclive.webadmin.mainscreen.team;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import org.icpclive.backend.player.urls.TeamUrls;
import org.icpclive.datapassing.TeamData;
import org.icpclive.events.TeamInfo;
import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.mainscreen.properties.TeamProperties;
import org.icpclive.webadmin.utils.RefreshableContent;
import org.icpclive.webadmin.utils.Utils;

import java.util.ArrayList;
import java.util.List;

@CssImport("./styles/shared-styles.css")
public class TeamView extends VerticalLayout implements RefreshableContent<VerticalLayout> {
    public static final String STATISTICS_SHOW_TYPE = "stats";
    private final TeamData data;
    private final Label status;
    private final Button showButton;
    private final Button hideButton;
    private final Button automatedShowButton;
    private final ComboBox<Integer> automatedNumber;
    private final TextField sleepTime;
    private final RadioButtonGroup<String> typeSelection;
    private final RadioButtonGroup<TeamInfo> teamSelection;

    public TeamView() {
        TeamProperties properties = MainScreenService.getProperties().getTeamProperties();
        data = MainScreenService.getInstance().getTeamData();
        status = new Label(getStatus());
        typeSelection = newTypeSelection();
        automatedShowButton = newAutomatedShowButton();

        automatedNumber = new ComboBox<>();
        automatedNumber.setItems(3, 4, 5, 8, 10, 12, 15, 20, 100);
        automatedNumber.setRequired(true);
        automatedNumber.setValue(10);

        sleepTime = new TextField("Sleep time");
        sleepTime.setValue(String.valueOf(properties.getSleepTime()));
        setSleepTime();

        teamSelection = newTeamSelection();

        showButton = new Button("Show info");
        showButton.addClickListener(event -> showTeam(true));

        hideButton = new Button("Stop");
        hideButton.addClickListener(event -> {
            if (data.inAutomaticShow()) {
                data.automaticStop();
            } else {
                data.setInfoManual(false, null, null, false);
                MainScreenService.getInstance().getTeamStatsData().setVisible(false, null);
            }
        });
        add(status);
        setHorizontalComponentAlignment(Alignment.CENTER, status);

        add(sleepTime);
        setHorizontalComponentAlignment(Alignment.CENTER, sleepTime);

        FormLayout controller = new FormLayout(showButton, hideButton, automatedNumber, automatedShowButton);
        controller.setWidth("50%");
        setHorizontalComponentAlignment(Alignment.CENTER, controller);
        add(controller);
        add(teamSelection);
        setHorizontalComponentAlignment(Alignment.CENTER, teamSelection);

    }

    private RadioButtonGroup<String> newTypeSelection() {
        RadioButtonGroup<String> typeSelection = new RadioButtonGroup<>();
        List<String> nonEmptyTypes = new ArrayList<>();
        for (String type : TeamUrls.types) {
            if (!type.isEmpty()) {
                nonEmptyTypes.add(type);
            }
        }
        typeSelection.setItems(nonEmptyTypes);
        typeSelection.setValue(TeamUrls.types[0]);
        return typeSelection;
    }

    private Button newAutomatedShowButton() {
        TeamData data = MainScreenService.getInstance().getTeamData();
        Button automatedShow = new Button("Show top teams");
        automatedShow.addClickListener(event -> {
            setSleepTime();
            if (data.inAutomaticShow()) {
                Notification.show("Automatic show is already on");
                return;
            }
            if (data.automaticStart(automatedNumber.getValue(), typeSelection.getValue(), true)) {
                Notification.show(automatedNumber.getValue() + " first teams are in automatic show");
            } else {
                long sleepTime = MainScreenService.getProperties()
                        .getTeamProperties().getSleepTime() / 1000;
                Notification.show("You need to wait " + sleepTime + " seconds first");
            }
        });
        return automatedShow;
    }

    private RadioButtonGroup<TeamInfo> newTeamSelection() {

        RadioButtonGroup<TeamInfo> teamSelection = new RadioButtonGroup<>();
        TeamProperties properties = MainScreenService.getProperties().getTeamProperties();
        teamSelection.setItems(properties.getTeamInfos());
        teamSelection.setRenderer(Utils.newTeamTextRenderer());
        teamSelection.setValue(properties.getTeamInfos()[0]);
        teamSelection.addValueChangeListener(event -> showTeam(false));
        return teamSelection;
    }

    private void showTeam(boolean withStats) {
        if (data.isVisible()) {
            if (data.inAutomaticShow()) {
                Notification.show("You need to stop automatic show first");
                return;
            }
            String selectedType = typeSelection.getValue();
            if (localLoad(selectedType)) {
                data.setSleepTime(0);
            } else {
                setSleepTime();
            }
            String type = STATISTICS_SHOW_TYPE.equals(selectedType) ? "" : selectedType;
            String outcome = data.setInfoManual(true, type, teamSelection.getValue(), withStats);
            if (outcome != null) {
                teamSelection.setValue(data.getTeam());
                Notification.show(outcome);
            }

        }
    }

    private void setSleepTime() {
        try {
            data.setSleepTime(Integer.parseInt(sleepTime.getValue()));
        } catch (Exception e) {
            Notification.show("Sleep time should be a number");
        }
    }

    public String getStatus() {
        if (data.inAutomaticShow()) {
            return data.automaticStatus();
        } else {
            String status = data.infoStatus();
            return Utils.getTeamStatus(status);
        }
    }

    private boolean localLoad(String type) {
        return STATISTICS_SHOW_TYPE.equals(type) || TeamUrls.localUrlType.contains(type);
    }

    public void refresh() {
        status.setText(getStatus());
    }

    public VerticalLayout getContent() {
        return this;
    }


}
