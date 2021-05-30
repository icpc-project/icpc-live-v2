package org.icpclive.webadmin.mainscreen.polls;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.icpclive.events.EventsLoader;
import org.icpclive.webadmin.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PollForm extends FormLayout {
    private final Button createPollButton;
    private final Button savePollButton;
    private final Button deletePollButton;
    private final Button cancelPollButton;
    private Poll pollOnEdit;
    private final TextField question;
    private final TextField hashtag;
    private final Checkbox teamOptions;
    private final Grid<Poll.Option> optionsTable;
    private final List<Poll.Option> optionsTableData;
    private final Button addOptionButton;
    private final Button removeOptionButton;
    private final TextField optionText;
    private final VerticalLayout optionsManager;
    private final VerticalLayout editForm;
    private final Grid<Poll> pollTable;


    public PollForm(Grid<Poll> pollTable) {
        this.pollTable = pollTable;
        PollsService service = PollsService.getInstance();

        createPollButton = new Button("New poll");
        createPollButton.addClickListener(event -> edit(null));

        savePollButton = newSavePollButton();

        deletePollButton = new Button("Delete");
        deletePollButton.addClickListener(event -> {
            service.removePoll(pollOnEdit);
            pollTable.getDataProvider().refreshAll();
            freePoll();
            Notification.show("Poll is deleted");
        });

        cancelPollButton = new Button("Cancel");
        cancelPollButton.addClickListener(event -> {
            freePoll();
            Notification.show("Editing was canceled");
        });

        question = new TextField("Question");
        hashtag = new TextField("Hashtag");

        teamOptions = newTeamOptions();

        optionText = new TextField("Option");

        optionsTableData = new ArrayList<>();
        optionsTable = new Grid<>();
        optionsTable.setItems(optionsTableData);
        optionsTable.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() == null) {
                optionText.clear();
            } else {
                optionText.setValue(event.getValue().getOption());
            }
        });
        optionsTable.setHeight("15rem");
        optionsTable.addColumn(Poll.Option::getOption).setHeader("Option");
        optionsTable.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

        addOptionButton = new Button("Add option");
        addOptionButton.addClickListener(event -> {
            optionsTableData.add(new Poll.Option(0, optionText.getValue(), 0));
            optionsTable.getDataProvider().refreshAll();
        });

        removeOptionButton = new Button("Remove option");
        removeOptionButton.addClickListener(event -> {
            optionsTable.getSelectionModel().getFirstSelectedItem().ifPresent(optionsTableData::remove);
            optionsTable.getDataProvider().refreshAll();
        });

        HorizontalLayout actions = new HorizontalLayout(createPollButton, savePollButton, deletePollButton, cancelPollButton);
        actions.setSpacing(true);
        FormLayout optionsActions = new FormLayout(addOptionButton, removeOptionButton, optionText);

        optionsManager = new VerticalLayout(optionsActions, optionsTable);
        optionsManager.setSizeFull();
        optionsManager.setSpacing(true);

        editForm = new VerticalLayout(actions, question, hashtag, teamOptions, optionsManager);
        editForm.setVisible(false);
        editForm.setSpacing(true);
        editForm.setMargin(true);
        editForm.setSizeFull();

        add(createPollButton, editForm);

    }


    private Checkbox newTeamOptions() {
        Checkbox teamOptions = new Checkbox("Use teams hashtags as options");
        teamOptions.addValueChangeListener(event -> {
            if (teamOptions.getValue()) {
                optionsManager.setVisible(false);
                optionsTableData.clear();
                optionsTable.getDataProvider().refreshAll();
            } else {
                optionsManager.setVisible(true);
            }
        });
        return teamOptions;
    }

    private Button newSavePollButton() {
        Button savePollButton = new Button("Save");
        PollsService service = PollsService.getInstance();
        savePollButton.addClickListener(event -> {
            if (pollOnEdit == null) {
                Poll poll;
                if (teamOptions.getValue()) {
                    poll = new Poll(question.getValue(), hashtag.getValue(), true);
                } else {
                    List<String> optionsHashtags = new ArrayList<>();
                    for (Poll.Option option : optionsTableData) {
                        optionsHashtags.add(option.getOption());
                    }
                    poll = new Poll(question.getValue(), hashtag.getValue(), optionsHashtags.toArray(new String[0]));
                }
                service.addPoll(poll);
                pollTable.getDataProvider().refreshAll();
             } else {
                synchronized (PollsService.getInstance().getPollsList()) {
                    pollOnEdit.setQuestion(question.getValue());
                    if (!hashtag.getValue().equals(pollOnEdit.getHashtag())) {
                        service.updateHashtag(pollOnEdit, hashtag.getValue());
                    }
                    pollOnEdit.setHashTag(hashtag.getValue());
                    if (teamOptions.getValue()) {
                        if (!pollOnEdit.getTeamOptions()) {
                            pollOnEdit.setOptions(EventsLoader.getInstance().getContestData().getHashTags());
                        }
                        pollOnEdit.setTeamOptions(true);
                    } else {
                        List<String> optionsHashtags = new ArrayList<>();
                        for (Poll.Option option : optionsTableData) {
                            optionsHashtags.add(option.getOption());
                        }
                        pollOnEdit.setOptions(optionsHashtags.toArray(new String[0]));
                    }
                    pollOnEdit.setTeamOptions(false);
                    pollTable.getDataProvider().refreshItem(pollOnEdit);
                }
            }
            editForm.setVisible(false);
            createPollButton.setVisible(true);
            String notification = (pollOnEdit == null) ? "New poll is created" : "The poll is edited";
            Notification.show(notification);
        });
        return savePollButton;
    }


    public void freePoll() {
        editForm.setVisible(false);
        createPollButton.setVisible(true);
    }

    public void edit(Poll poll) {
        createPollButton.setVisible(false);
        pollOnEdit = poll;
        editForm.setVisible(true);
        if (poll == null) {
            question.clear();
            hashtag.clear();
            optionsManager.setVisible(true);
            teamOptions.clear();
            optionsTableData.clear();
            optionsTable.getDataProvider().refreshAll();
        } else {
            question.setValue(poll.getQuestion());
            hashtag.setValue(poll.getHashtag());
            if (poll.getTeamOptions()) {
                teamOptions.setValue(true);
                optionsManager.setVisible(false);
            } else {
                teamOptions.setValue(false);
                optionsTableData.clear();
                optionsTableData.addAll(Arrays.asList(poll.getOptions()));
                optionsTable.getDataProvider().refreshAll();
            }
        }

    }
}
