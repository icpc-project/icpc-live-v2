package org.icpclive.webadmin.utils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class DemonstratorController<T> extends Controller<T> implements Refreshable {
    private final List<Label> statuses;
    private final List<Button> showButtons;
    private final List<Button> hideButtons;
    private final String[] captions;
    private final Button showAllButton;
    private final Button hideAllButton;

    public DemonstratorController(final String[] captions, final List<String> dataTextFieldHeaders, final Class<T> bean, final int tableCount, final List<T> container) {
        super(dataTextFieldHeaders, bean, tableCount, container);
        this.captions = captions;
        statuses = new ArrayList<>();
        showButtons = new ArrayList<>();
        hideButtons = new ArrayList<>();
        HorizontalLayout visionController = new HorizontalLayout();
        visionController.setWidth("100%");
        for (int i = 0; i < getTableCount(); i++) {
            statuses.add(new Label("Nothing is shown"));
            showButtons.add(newShowAdvertisementButton(i));
            hideButtons.add(newHideAdvertisementButton(i));
            statuses.get(i).setWidth("100%");
            FormLayout layout = new FormLayout(statuses.get(i), showButtons.get(i), hideButtons.get(i));
            visionController.add(layout);
        }
        showAllButton = newShowButtonFromRange(0, getTableCount());
        hideAllButton = newHideButtonFromRange(0, getTableCount());
        add(visionController, new FormLayout(showAllButton, hideAllButton));


    }
    public DemonstratorController(final String[] captions, final String[] dataTextFieldHeaders, final Class<T> bean, final int tableCount, final List<T> container) {
        this(captions, Arrays.asList(dataTextFieldHeaders), bean, tableCount, container);
    }


    private Button newShowAdvertisementButton(int id) {
        Button button = newShowButtonFromRange(id, id + 1);
        button.setText("Show " + captions[id]);
        return button;
    }

    private Button newHideAdvertisementButton(int id) {
        Button button = newHideButtonFromRange(id, id + 1);
        button.setText("Hide " + captions[id]);
        return button;
    }

    private Button newShowButtonFromRange(int start, int end) {
        Button showButtonFromRange = new Button("Show all");
        showButtonFromRange.addClickListener(event -> {
            for (int i = start; i < end; i++) {
                Optional<T> item = getTable(i).getSelectionModel().getFirstSelectedItem();
                if (!item.isPresent()) {
                    Notification.show("You need to choose " + captions[i]);
                    return;
                }
            }
            for (int i = start; i < end; i++) {
                Optional<T> item = getTable(i).getSelectionModel().getFirstSelectedItem();
                if (item.isPresent()) {
                    String outcome = setVisible(true, item.get(), i);
                    if (outcome != null) {
                        Notification.show(outcome);
                        return;
                    }
                    statuses.get(i).setText(getStatus(i));
                }
            }
        });
        return showButtonFromRange;
    }

    private Button newHideButtonFromRange(int start, int end) {
        Button showButtonFromRange = new Button("Hide all");
        showButtonFromRange.addClickListener(event -> {
            for (int i = start; i < end; i++) {
                Optional<T> item = getTable(i).getSelectionModel().getFirstSelectedItem();
                setVisible(false, item.orElse(null), i);
                statuses.get(i).setText(getStatus(i));
            }
        });
        return showButtonFromRange;
    }

    protected void deleteDemonstrateAllButtons() {
        showAllButton.setVisible(false);
        hideAllButton.setVisible(false);
    }


    protected void deleteHideButtons() {
        for (Button hideButton : hideButtons) {
            hideButton.setVisible(false);
        }
    }

    public void refresh() {
        for (int i = 0; i < getTableCount(); i++) {
            statuses.get(i).setText(getStatus(i));
            getTable(i).getDataProvider().refreshAll();
        }
    }



    protected abstract String getStatus(int id);

    protected abstract String setVisible(boolean visible, T item, int id);

}
