package org.icpclive.webadmin.creepingline;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.icpclive.webadmin.mainscreen.loaders.TwitterLoader;
import org.icpclive.webadmin.utils.Refreshable;
import org.icpclive.webadmin.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageForm extends VerticalLayout implements Refreshable {
    private final MessageService service;
    private Message lastMessage;
    private final TextField messageText;
    private final ComboBox<Integer> timeBox;
    private final Checkbox requireAdvertisement;
    private final List<Component> hidingComponents;
    private final Grid<Message> messageTable;

    public MessageForm() {
        service = MessageService.getInstance();
        lastMessage = null;

        messageTable = newMessageTable();

        Button newMessageButton = new Button("New message");
        newMessageButton.addClickListener(event -> {
            edit(null);
        });
        messageText = new TextField("Message");

        timeBox = newTimeBox();

        requireAdvertisement = new Checkbox("Advertisement");

        Button saveButton = newSaveButton();

        Button deleteButton = new Button("Delete");
        deleteButton.addClickListener(event -> {
            Optional<Message> message = messageTable.getSelectionModel().getFirstSelectedItem();
            message.ifPresent(service::removeMessage);
            messageTable.getDataProvider().refreshAll();
            setVisibility(false);
            Notification.show("Deleted");
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> {
            setVisibility(false);
            Notification.show("Cancelled");
        });

        Button postButton = new Button("Tweet");
        postButton.addClickListener(event -> {
            TwitterLoader.getInstance().postMessage(messageText.getValue()) ;
            messageText.clear();
            setVisibility(false);
            Notification.show("Twitted message");
        });
        hidingComponents = new ArrayList<>();
        hidingComponents.add(saveButton);
        hidingComponents.add(deleteButton);
        hidingComponents.add(cancelButton);
        hidingComponents.add(postButton);
        hidingComponents.add(timeBox);
        hidingComponents.add(requireAdvertisement);


        FormLayout controller = new FormLayout(newMessageButton, saveButton, deleteButton, cancelButton, postButton);
        controller.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 5));

        FormLayout settings = new FormLayout(controller, messageText, timeBox, requireAdvertisement);
        settings.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        setVisibility(false);
        add(settings, messageTable);

    }

    private Grid<Message> newMessageTable() {
        Grid<Message> messageTable = new Grid<>();
        messageTable.addColumn(Message::getMessage).setHeader("Message").setFlexGrow(6);
        messageTable.addColumn(Message::isAdvertisement).setHeader("Is advertisement").setFlexGrow(1);
        messageTable.addColumn(message -> (message.getEndTime() - System.currentTimeMillis()) / 1000).setHeader("time").setFlexGrow(1);
        messageTable.setItems(service.getMessageContainer());
        messageTable.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        messageTable.setHeightByRows(true);
        messageTable.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() == null) {
                setVisibility(false);
            } else {
                edit(event.getValue());
            }
        });
        return messageTable;
    }

    private ComboBox<Integer> newTimeBox() {
        ComboBox<Integer> timeBox = new ComboBox<>("Duration");
        Integer[] durations = new Integer[]{30000, 60000, 120000, 300000, 600000, 900000, 1800000, Integer.MAX_VALUE};
        timeBox.setItems(durations);
        timeBox.setRequired(true);
        timeBox.setItemLabelGenerator(duration -> {
            if (duration == Integer.MAX_VALUE) {
                return "infinity";
            } else {
                return getDurationAsString(duration);
            }
        });
        timeBox.setValue(30000);
        return timeBox;
    }

    private Button newSaveButton() {
        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            int time = timeBox.getValue();
            if (lastMessage == null) {
                service.addMessage(new Message(messageText.getValue(),
                        System.currentTimeMillis(), time, requireAdvertisement.getValue()));
            } else {
                service.setMessageValues(lastMessage,
                        messageText.getValue(), requireAdvertisement.getValue());
            }
            messageText.clear();
            setVisibility(false);
            messageTable.getDataProvider().refreshAll();
            Notification.show((lastMessage == null) ? "Created new message" : "Edited message");
        });
        return saveButton;
    }


    private String getDurationAsString(int duration) {
        int minutes = (duration / 1000) / 60;
        int seconds = (duration / 1000) % 60;
        String minutesAsString = (minutes == 0) ? "" : minutes + " minutes";
        String secondsAsString = (seconds == 0) ? "" : seconds + " seconds";
        return minutesAsString + secondsAsString;

    }

    private void setVisibility(boolean visibility) {
        for (Component component : hidingComponents) {
            component.setVisible(visibility);
        }
    }

    private void edit(Message messageToEdit) {
        lastMessage = messageToEdit;
        setVisibility(true);
        if (lastMessage != null) {
            messageText.setValue(messageToEdit.getMessage());
            requireAdvertisement.setValue(messageToEdit.isAdvertisement());
            timeBox.setVisible(false);
        } else {
            messageText.clear();
            timeBox.setVisible(true);
            timeBox.setValue(30000);
            requireAdvertisement.setValue(false);
        }
    }

    public void editOutside(Message messageToEdit) {
        if (messageToEdit == null) {
            return;
        }
        lastMessage = null;
        messageText.clear();
        timeBox.setValue(30000);
        String source = messageToEdit.getSource();
        String value = (source == null || source.isEmpty()) ? "" : source + ": ";
        value += messageToEdit.getMessage();
        messageText.setValue(value);
        requireAdvertisement.setValue(messageToEdit.isAdvertisement());
        setVisibility(true);
    }

    @Override
    public void refresh() {
        messageTable.getDataProvider().refreshAll();
    }
}
