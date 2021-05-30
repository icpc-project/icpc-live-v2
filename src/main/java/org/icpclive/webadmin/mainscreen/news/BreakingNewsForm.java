package org.icpclive.webadmin.mainscreen.news;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.icpclive.backend.player.urls.TeamUrls;
import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.utils.Refreshable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class BreakingNewsForm extends FormLayout implements Refreshable {
    private static final Logger log = LogManager.getLogger(BreakingNewsForm.class);
    private final TextField sleepTime;
    private final Label status;
    private final Checkbox liveBox;
    private final RadioButtonGroup<String> liveTypes;
    private final Label messageToShow;
    private final List<String> predefinedMessages;
    private final ComboBox<String> predefinedMessagesBox;
    private final TextField patternCreatorField;
    private final Button patternCreatorButton;

    private final Button showRunButton;
    private final Button hideRunButton;

    private final TextField teamProblem;
    private final ComboBox<String> outcomes;
    private final TextField time;
    int currentRunId;


    public BreakingNewsForm() {
        setResponsiveSteps(new ResponsiveStep("0", 1));
        this.status = newStatus();
        this.sleepTime = new TextField("Sleep time");
        add(sleepTime);
        this.liveBox = newLiveBox();
        this.liveTypes = newLiveTypes();
        this.messageToShow = new Label("Message");
        add(messageToShow);
        this.predefinedMessages = new ArrayList<>();
        this.predefinedMessagesBox = newPredefinedMessages();
        this.patternCreatorField = new TextField("New pattern");
        add(patternCreatorField);
        this.patternCreatorButton = newPatternCreatorButton();

        teamProblem = new TextField("Team problem");
        teamProblem.addValueChangeListener(event -> predefinedMessages.clear());

        time = new TextField("Time");

        outcomes = new ComboBox<>("Outcome");
        outcomes.setItems("AC", "WA", "TLE", "RTE", "PE", "CE", "Frozen");
        outcomes.addValueChangeListener(event -> predefinedMessages.clear());

        showRunButton = new Button("Show");
        showRunButton.addClickListener(event -> {
           if (teamProblem.getValue().isEmpty()) {
               Notification.show("It requires team id and problem id");
           } else {
               int teamId, problemId;
               try {
                   String[] teamProblemInfo = teamProblem.getValue().split(" ");
                   teamId = Integer.parseInt(teamProblemInfo[0]) - 1;
                   problemId = teamProblemInfo[1].charAt(0) - 'A';
               } catch (NumberFormatException e) {
                   Notification.show("Field team problem should contain id of the team and problem alias");
                   return;
               }
               updateMessageField();
               if (TeamUrls.localUrlType.contains(liveTypes.getValue())) {
                   try {
                       MainScreenService.getInstance().getBreakingNewsData().sleepTime
                               = Integer.parseInt(sleepTime.getValue());
                   } catch (NumberFormatException e) {
                       MainScreenService.getInstance().getBreakingNewsData().sleepTime
                               = MainScreenService.getProperties().getTeamProperties().getSleepTime();
                   }
               }
               String outcome = MainScreenService.getInstance().getBreakingNewsData()
                       .setNewsVisible(true, liveTypes.getValue(), liveBox.getValue(), messageToShow.getText(),
                               teamId, problemId, currentRunId);
               if (outcome != null) {
                   Notification.show(outcome);
               } else {
                   teamProblem.clear();
                   outcomes.clear();
                   time.clear();
                   predefinedMessages.clear();
                   patternCreatorField.clear();
                   currentRunId = 1;
               }
               status.setText(getBreakingNewsStatus());
           }
        });

        hideRunButton = new Button("Hide");
        hideRunButton.addClickListener(event -> {
           String outcome = MainScreenService.getInstance().getBreakingNewsData()
                   .setNewsVisible(false, null, liveBox.getValue(), "", -1, -1,-1);
           if (outcome != null) {
               Notification.show(outcome);
           }
           status.setText(getBreakingNewsStatus());
        });

        FormLayout teamInfo = new FormLayout(teamProblem, time, outcomes);
        teamInfo.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 3));

        FormLayout hideAndShow = new FormLayout(showRunButton, hideRunButton);

        add(teamInfo, hideAndShow);
    }

    private void updateMessageField() {
        String result = patternCreatorButton.getText();
        if (result.isEmpty()) {
            result = predefinedMessagesBox.getValue();
        }
        if (!teamProblem.isEmpty()) {
            try {
                String[] teamProblemInfo = teamProblem.getValue().split(" ");
                int teamId = Integer.parseInt(teamProblemInfo[0]) - 1;
                String teamName = (teamId == -1) ? "" : MainScreenService.getProperties()
                        .getTeamProperties().getContestInfo().getParticipant(teamId).getName();
                result = result.replace("%team", teamName);
                result = result.replace("%problem", teamProblemInfo[1]);
            } catch (Exception e) {
                Notification.show("Incorrect team-problem pair");
                return;
            }
        }
        if (!time.isEmpty()) {
            result = result.replace("%time", time.getValue());
        }
        if (!outcomes.isEmpty()) {
            result = result.replace("%outcome", outcomes.getValue());
        }
        messageToShow.setText(result);
    }


    private Label newStatus() {
        Label status = new Label(getBreakingNewsStatus());
        status.getStyle().set("font-size", "large");
        add(status);
        return status;
    }

    private RadioButtonGroup<String> newLiveTypes() {
        RadioButtonGroup<String> liveTypes = new RadioButtonGroup<>();
        liveTypes.setItems(TeamUrls.types);
        liveTypes.setValue(TeamUrls.types[0]);
        liveTypes.setEnabled(true);
        add(liveTypes);
        return liveTypes;
    }

    private Checkbox newLiveBox() {
        Checkbox liveBox = new Checkbox("is live");
        liveBox.setValue(true);
        liveBox.addValueChangeListener(event -> {
            liveTypes.setEnabled(liveBox.getValue());
            status.setText(getBreakingNewsStatus());
        });
        add(liveBox);
        return liveBox;
    }

    private ComboBox<String> newPredefinedMessages() {
        ComboBox<String> predefinedMessages = new ComboBox<>("Patterns");
        Path patternsFile = Paths.get(MainScreenService.getProperties().getBreakingNewsProperties().getPatternsFileName());
        if (Files.exists(patternsFile)) {
            try {
                this.predefinedMessages.addAll(Files.readAllLines(patternsFile));
            } catch (IOException e) {
                log.error("Error during reading patterns file");
            }
        }
        predefinedMessages.setItems(this.predefinedMessages);
        predefinedMessages.setAllowCustomValue(true);
        add(predefinedMessages);
        return predefinedMessages;
    }

    private Button newPatternCreatorButton() {
        Button patternCreatorButton = new Button("Save");
        Path patternsFile = Paths.get(MainScreenService.getProperties().getBreakingNewsProperties().getPatternsFileName());
        patternCreatorButton.addClickListener(event -> {
            if (patternCreatorField.isEmpty()) {
                Notification.show("Message should not be empty");
            } else {
                try {
                    StandardOpenOption openOption = Files.exists(patternsFile) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE;
                    String pattern = patternCreatorField.getValue() + System.lineSeparator();
                    Files.write(patternsFile, pattern.getBytes(), openOption);

                    predefinedMessages.add(pattern);
                    predefinedMessagesBox.getDataProvider().refreshAll();
                    patternCreatorField.clear();
                } catch (IOException e) {
                    log.error("Error during writing new patterns");
                }
            }

        });
        add(patternCreatorButton);
        return patternCreatorButton;
    }

    private String getBreakingNewsStatus() {
        return MainScreenService.getInstance().getBreakingNewsData().getStatus();
    }

    public void update(final BreakingNews news) {
        if (news == null) {
            teamProblem.clear();
            time.clear();
            outcomes.clear();
            currentRunId = -1;
        } else {
            teamProblem.setValue(news.getTeamId() + " " + news.getProblem());
            time.setValue(String.valueOf(news.getTimestamp()));
            outcomes.setValue(news.getOutcome());
            currentRunId = news.getRunId();
            updateMessageField();
        }
    }
    public void refresh() {
        status.setText(getBreakingNewsStatus());
    }
}
