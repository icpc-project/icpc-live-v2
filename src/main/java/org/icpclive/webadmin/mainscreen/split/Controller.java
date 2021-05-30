package org.icpclive.webadmin.mainscreen.split;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import org.icpclive.backend.player.urls.TeamUrls;
import org.icpclive.datapassing.SplitScreenData;
import org.icpclive.events.TeamInfo;
import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.utils.Refreshable;
import org.icpclive.webadmin.utils.Utils;


public class Controller extends VerticalLayout implements Refreshable {
    private final SplitScreenData data;
    private final int id;
    private final Label status;
    private  final Checkbox automated;
    private final RadioButtonGroup<String> types;
    private  final TextField team;
    private final Button show;
    private final Button hide;
    private long lastChangeTimestamp = 0;

    public Controller(int id) {
        this.id = id;
        this.data = MainScreenService.getInstance().getSplitScreenData();

        status = new Label("Controller " + (id + 1) + " (" + getTeamStatus() + ")");

        automated = new Checkbox("Automated");
        automated.setValue(true);
        automated.addValueChangeListener(event -> {
           enableComponents();
           if (automated.getValue()) {
               data.isAutomatic[id] = true;
               data.timestamps[id] = System.currentTimeMillis();
               data.setInfoVisible(id, false, null, null);
           }
        });
        types = new RadioButtonGroup<>();
        types.setItems(TeamUrls.types);
        types.setRequired(true);
        types.setValue(TeamUrls.types[0]);
        types.setEnabled(false);

        team = new TextField("Team: ");
        team.setSizeFull();

        show = new Button("Show");
        show.addClickListener(event -> {
           data.isAutomatic[id] = false;
           data.timestamps[id]= System.currentTimeMillis();
           try {
               int teamId = Integer.parseInt(team.getValue());
               TeamInfo teamInfo = MainScreenService.getProperties().getTeamProperties().getContestInfo().getParticipant(teamId);
               if (teamInfo == null) {
                   Notification.show("There is not team with this id " + teamId);
               }
               String outcome = data.setInfoVisible(id, true, types.getValue(), teamInfo);
               if (outcome != null) {
                   Notification.show(outcome);
               }
           } catch (NumberFormatException e) {
               Notification.show("Expected team id number");
           }
        });
        show.setEnabled(false);

        hide = new Button("Hide");
        hide.addClickListener(event -> {
           if (!automated.isEmpty()) {
               Notification.show("You can not use hide button in automatic mode");
           } else {
               data.setInfoVisible(id, false, null, null);
           }
        });
        hide.setEnabled(false);
        FormLayout modes = new FormLayout(status, automated, types);
        modes.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        FormLayout dataInput = new FormLayout(team, show, hide);
        dataInput.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 3));
        add(modes, dataInput);
    }
    public void enableComponents() {
        boolean auto = automated.getValue();
        types.setValue(auto ? null : TeamUrls.types[0]);
        types.setEnabled(!auto);
        show.setEnabled(!auto);
        hide.setEnabled(!auto);
    }

    public String getTeamStatus() {
        if (data.isAutomatic[id]) {
            return  "Screen in automatic mode";
        }
        String status = data.infoStatus(id);
        return Utils.getTeamStatus(status);
    }
    public void refresh() {
        status.setText("Controller " + (id + 1) + " (" + getTeamStatus() + ")");
        if (data.timestamps[id] > lastChangeTimestamp) {
            automated.setValue(data.isAutomatic[id]);
            enableComponents();
            lastChangeTimestamp = data.timestamps[id];
        }
    }

}
