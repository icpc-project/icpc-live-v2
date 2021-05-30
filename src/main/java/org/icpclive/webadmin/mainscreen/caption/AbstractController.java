//package org.icpclive.webadmin.mainscreen.caption;
//
//import com.vaadin.flow.component.button.Button;
//import com.vaadin.flow.component.formlayout.FormLayout;
//import com.vaadin.flow.component.grid.Grid;
//import com.vaadin.flow.component.grid.GridVariant;
//import com.vaadin.flow.component.html.Label;
//import com.vaadin.flow.component.notification.Notification;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
//import com.vaadin.flow.component.textfield.TextField;
//
//import java.util.List;
//import java.util.Optional;
//
//abstract class AbstractController extends VerticalLayout {
//    private final String header;
//    private final Label statusLabel;
//    private final TextField valueText;
//    private final Button addButton;
//    private final Button editButton;
//    private final Button removeButton;
//    private final Button discardButton;
//    private final Grid<Advertisement> table;
//
//    AbstractController(final String controllerHeader, final List<Advertisement> container) {
//        if (controllerHeader.isEmpty()) {
//            throw new IllegalArgumentException("Header can't be empty");
//        }
//        header = controllerHeader;
//        statusLabel = new Label(getStatus());
//        statusLabel.getStyle().set("font-size", "large");
//
//        String firstLetterCapitalizedHeader = header.substring(0, 1).toUpperCase() + header.substring(1);
//        valueText = new TextField(firstLetterCapitalizedHeader + ": ");
//
//        addButton = newAddAdvertisementButton();
//        editButton = newEditAdvertisementButton();
//        removeButton = newRemoveAdvertisementButton();
//        discardButton = newDiscardAdvertisementButton();
//        FormLayout advertisementChangers = new FormLayout(valueText, addButton,
//                editButton, removeButton, discardButton);
//        advertisementChangers.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 5));
//
//
//        table = newTable(container);
//
//        add(statusLabel, advertisementChangers, table);
//        setDefaultValues();
//
//    }
//
//    public abstract String getStatus();
//
//    protected abstract void recache();
//
//    protected abstract void setItemValue(Advertisement item, String value);
//
//    protected abstract void removeItem(Advertisement item);
//
//    protected abstract void addItem(final Advertisement item);
//
//
//    private Button newAddAdvertisementButton() {
//        Button addAdvertisementButton = new Button("Add new");
//        addAdvertisementButton.addClickListener(event -> {
//            addItem(new Advertisement(valueText.getValue()));
//            valueText.clear();
//            table.getDataProvider().refreshAll();
//            setDefaultValues();
//            recache();
//        });
//        return addAdvertisementButton;
//    }
//
//    private Button newEditAdvertisementButton() {
//        Button editAdvertisementButton = new Button("Edit");
//        editAdvertisementButton.addClickListener(event -> table.getSelectionModel().getFirstSelectedItem()
//                .ifPresent(item -> {
//                    setItemValue(item, valueText.getValue());
//                    table.getDataProvider().refreshItem(item);
//                    valueText.clear();
//                    setDefaultValues();
//                    recache();
//                }));
//        return editAdvertisementButton;
//    }
//
//    private Button newRemoveAdvertisementButton() {
//        Button removeAdvertisementButton = new Button("Remove");
//        removeAdvertisementButton.addClickListener(event -> {
//            Optional<Advertisement> item = table.getSelectionModel().getFirstSelectedItem();
//            if (item.isPresent()) {
//                removeItem(item.get());
//                table.getDataProvider().refreshAll();
//            } else {
//                Notification.show("You should choose " + header);
//            }
//            valueText.clear();
//            setDefaultValues();
//            recache();
//        });
//        return removeAdvertisementButton;
//
//    }
//
//    private Button newDiscardAdvertisementButton() {
//        Button discardAdvertisementButton = new Button("Discard");
//        discardAdvertisementButton.addClickListener(event -> {
//            valueText.clear();
//            setDefaultValues();
//        });
//        return discardAdvertisementButton;
//
//    }
//
//    private Grid<Advertisement> newTable(List<Advertisement> container) {
//        Grid<Advertisement> advertisementTable = new Grid<>();
//        advertisementTable.setItems(container);
//        advertisementTable.addColumn(Advertisement::getAdvertisement).setHeader(header);
//        advertisementTable.setWidth("100%");
//        advertisementTable.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
//        advertisementTable.setHeightByRows(true);
//        advertisementTable.asSingleSelect().addValueChangeListener(event -> {
//            if (event.getValue() == null) {
//                setDefaultValues();
//            } else {
//                editButton.setVisible(true);
//                removeButton.setVisible(true);
//                discardButton.setVisible(true);
//                valueText.setValue(event.getValue().getAdvertisement());
//            }
//        });
//        return advertisementTable;
//    }
//
//
//    private void setDefaultValues() {
//        editButton.setVisible(false);
//        removeButton.setVisible(false);
//        discardButton.setVisible(false);
//    }
//    protected Label getStatusLabel() {
//        return statusLabel;
//    }
//
//    protected TextField getValueText() {
//        return valueText;
//    }
//
//    protected Button getAddButton() {
//        return addButton;
//    }
//
//    protected Button getEditButton() {
//        return editButton;
//    }
//
//    protected Button getRemoveButton() {
//        return removeButton;
//    }
//
//    protected Button getDiscardButton() {
//        return discardButton;
//    }
//
//    protected Grid<Advertisement> getTable() {
//        return table;
//    }
//}
