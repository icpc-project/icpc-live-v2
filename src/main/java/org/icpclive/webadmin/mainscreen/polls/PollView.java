package org.icpclive.webadmin.mainscreen.polls;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.icpclive.datapassing.PollData;
import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.utils.RefreshableContent;

import java.util.Optional;

public class PollView extends HorizontalLayout implements RefreshableContent<HorizontalLayout> {
    public static final String NAME = "pollManager";
    private final PollForm pollForm;
    private final Grid<Poll> pollTable;
    private final Label status;
    private final Button showPollButton;
    private final Button hidePollButton;


    public PollView() {
        PollData data = MainScreenService.getInstance().getPollData();
        pollTable = newPollTable();
        pollForm = new PollForm(pollTable);
        pollForm.setSizeFull();

        status = new Label("Poll overlay status");
        showPollButton = new Button("Show poll");
        showPollButton.addClickListener(event -> {
            Optional<Poll> pollToShow = pollTable.getSelectionModel().getFirstSelectedItem();
            if (!pollToShow.isPresent()) {
                Notification.show("You need to choose the poll in the table");
                return;
            }
            String result = data.setPollVisible(pollToShow.get());
            if (result != null) {
                Notification.show(result);
            }
        });
        hidePollButton = new Button("Hide poll");
        hidePollButton.addClickListener(event -> data.hide());

        HorizontalLayout pollShower = new HorizontalLayout(showPollButton, hidePollButton);
        VerticalLayout pollController = new VerticalLayout(status, pollShower, pollForm);
        pollController.setSizeFull();
        pollController.setSpacing(true);
        pollController.setMargin(true);
        add(pollTable, pollController);



    }

    private Grid<Poll> newPollTable() {
        Grid<Poll> pollTable = new Grid<>();
        pollTable.setItems(PollsService.getInstance().getPollsList());
        pollTable.addColumn(Poll::getQuestion).setHeader("Question").setFlexGrow(6);
        pollTable.addColumn(Poll::getHashtag).setHeader("Hashtag").setFlexGrow(3);
        pollTable.addColumn(Poll::getTeamOptions).setHeader("Team options").setFlexGrow(1);
        pollTable.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() == null) {
                pollForm.freePoll();
            } else {
                pollForm.edit(event.getValue());
            }
        });
        pollTable.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        pollTable.setSizeFull();
        return pollTable;
    }

    @Override
    public HorizontalLayout getContent() {
        return this;
    }

    @Override
    public void refresh() {
        status.setText(MainScreenService.getInstance().getPollData().toString());
    }
}
