package org.icpclive.webadmin.utils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Controller<T> extends VerticalLayout {
    private final Class<T> beanType;
    private final List<TextField> dataTextFields;
    private final Button addButton;
    private final Button editButton;
    private final Button removeButton;
    private final Button discardButton;
    private final List<Grid<T>> tables;
    private T lastItem;

    public Controller(String[] dataTextFieldHeaders, Class<T> bean, int tableCount, final List<T> container) {
        this(Arrays.asList(dataTextFieldHeaders), bean, tableCount, container);
    }

    public Controller(List<String> dataTextFieldHeaders, Class<T> bean, int tableCount, final List<T> container) {
        lastItem = null;
        beanType = bean;

        dataTextFields = new ArrayList<>();
        for (String header : dataTextFieldHeaders) {
            dataTextFields.add(new TextField(header));
        }

        addButton = newAddButton();
        editButton = newEditButton();
        removeButton = newRemoveButton();
        discardButton = newDiscardButton();

        FormLayout changers = new FormLayout(addButton,
                editButton, removeButton, discardButton);
        changers.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));

        FormLayout fields = new FormLayout(dataTextFields.toArray(new TextField[0]));
        fields.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        FormLayout dataManager = new FormLayout(changers, fields);
        dataManager.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        tables = new ArrayList<>();
        for (int i = 0; i < tableCount; i++) {
            tables.add(newTable(container));
        }
        HorizontalLayout tableLayout = new HorizontalLayout();
        tableLayout.setWidth("100%");
        for (Grid<T> table : tables) {
            tableLayout.add(table);
        }
        add(dataManager, tableLayout);
        setDefaultValues();

    }

    protected abstract void recache();

    protected abstract void setItemValue(T item, List<String> values);

    protected abstract void removeItem(T item);

    protected abstract void addItem(final List<String> values);

    protected abstract List<String> getTextFieldValuesFromItem(T item);


    private Button newAddButton() {
        Button addButton = new Button("Add new");
        addButton.addClickListener(event -> {
            addItem(getTextFieldValues());
            clearTextFields();
            for (Grid<T> table : tables) {
                table.getDataProvider().refreshAll();
            }
            setDefaultValues();
            recache();
        });
        return addButton;
    }

    private Button newEditButton() {
        Button editButton = new Button("Edit");
        editButton.addClickListener(event -> {
            if (lastItem != null) {
                setItemValue(lastItem, getTextFieldValues());
                clearTextFields();
                setDefaultValues();
                for (Grid<T> table : tables) {
                    table.getDataProvider().refreshItem(lastItem);
                }
                recache();
            }
        });
        return editButton;
    }

    private Button newRemoveButton() {
        Button removeButton = new Button("Remove");
        removeButton.addClickListener(event -> {
            if (lastItem != null) {
                removeItem(lastItem);
                for (Grid<T> table : tables) {
                    table.getDataProvider().refreshAll();
                }
                lastItem = null;
            } else {
                Notification.show("You should choose item");
            }
            clearTextFields();
            setDefaultValues();
            recache();
        });
        return removeButton;

    }

    private Button newDiscardButton() {
        Button discardButton = new Button("Discard");
        discardButton.addClickListener(event -> {
            clearTextFields();
            setDefaultValues();
        });
        return discardButton;

    }

    private Grid<T> newTable(List<T> container) {
        Grid<T> table = new Grid<>(beanType);
        table.setItems(container);
        table.setWidth("100%");
        table.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        table.setHeightByRows(true);
        table.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() == null) {
                setDefaultValues();
            } else {
                lastItem = event.getValue();
                editButton.setVisible(true);
                removeButton.setVisible(true);
                discardButton.setVisible(true);
                List<String> values = getTextFieldValuesFromItem(lastItem);
                for (int i = 0; i < values.size(); i++) {
                    dataTextFields.get(i).setValue(values.get(i));
                }
            }
        });
        return table;
    }

    private void clearTextFields() {
        for (TextField textField : dataTextFields) {
            textField.clear();
        }
    }

    private List<String> getTextFieldValues() {
        List<String> values = new ArrayList<>();
        for (TextField textField : dataTextFields) {
            values.add(textField.getValue());
        }
        return values;
    }


    private void setDefaultValues() {
        editButton.setVisible(false);
        removeButton.setVisible(false);
        discardButton.setVisible(false);
    }

    protected List<TextField> getDataTextFields() {
        return dataTextFields;
    }

    protected Button getAddButton() {
        return addButton;
    }

    protected Button getEditButton() {
        return editButton;
    }

    protected Button getRemoveButton() {
        return removeButton;
    }

    protected Button getDiscardButton() {
        return discardButton;
    }

    protected Grid<T> getTable(int id) {
        return tables.get(id);
    }

    protected int getTableCount() {
        return tables.size();
    }
}
