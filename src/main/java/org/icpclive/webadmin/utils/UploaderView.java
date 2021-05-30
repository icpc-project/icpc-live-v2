package org.icpclive.webadmin.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import org.icpclive.datapassing.CachedData;
import org.icpclive.datapassing.PictureData;
import org.icpclive.webadmin.mainscreen.MainScreenService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;


public abstract class UploaderView<T, U extends Component> extends HorizontalLayout implements RefreshableContent<HorizontalLayout> {
    private final CachedData data;
    private final Label status;
    private final Grid<T> table;
    private final TextField captionAdd;
    private final Upload upload;
    private final Button edit;
    private final Button show;
    private final Button hide;
    private final TextField caption;
    private final U itemComponent;
    private T lastItem;

    public UploaderView(CachedData inputData, String typeName) {
        data = inputData;
        status = new Label(data.toString());
        table = newTable();

        captionAdd = new TextField("Caption to " + typeName);
        upload = new Upload(new Uploader());
        upload.addAllFinishedListener(event -> {
            captionAdd.clear();
            table.getDataProvider().refreshAll();
        });
        upload.setUploadButton(new Button("Upload"));

        caption = new TextField("Caption");
        caption.setVisible(false);

        itemComponent = newItemComponent();
        itemComponent.setVisible(false);

        show = new Button("Show");
        show.addClickListener(event -> {
            Optional<T> item = table.asSingleSelect().getOptionalValue();
            if (!item.isPresent()) {
                Notification.show("You should choose a " + typeName);
                return;
            }
            String outcome = setVisibleItem(item.get());
            if (outcome != null) {
                Notification.show(outcome);
            }
        });
        hide = new Button("Hide");
        hide.addClickListener(event -> hideContent());
        edit = new Button("Edit");
        edit.addClickListener(event -> {
            Optional<T> item = table.asSingleSelect().getOptionalValue();
            if (!item.isPresent()) {
                return;
            }
            setCaption(caption.getValue(), item.get());
            table.getDataProvider().refreshAll();
            edit.setVisible(false);
            itemComponent.setVisible(false);
        });
        edit.setVisible(false);

        FormLayout editors = new FormLayout(hide, show, edit);
        editors.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 3));
        FormLayout controller = new FormLayout(status, editors, caption, itemComponent);
        controller.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        FormLayout uploadLayout = new FormLayout(captionAdd, upload);


        add(new VerticalLayout(uploadLayout, controller), table);


    }

    private Grid<T> newTable() {
        Grid<T> table = new Grid<>();
        table.setItems(getContainer());
        table.addColumn(this::getCaption).setHeader("caption");
        table.setHeightByRows(true);
        table.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        table.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() == null) {
                caption.setVisible(false);
                itemComponent.setVisible(false);
                edit.setVisible(false);
            } else {
                lastItem = event.getValue();
                caption.setValue(getCaption(lastItem));
                caption.setVisible(true);
                setSource(itemComponent, lastItem);
                itemComponent.setVisible(true);
                edit.setVisible(true);
            }
        });
        return table;
    }

    protected abstract U newItemComponent();


    protected abstract String setVisibleItem(final T item);

    protected abstract void hideContent();

    protected abstract void setCaption(final String caption, final T item);

    protected abstract void setSource(U itemComponent, T lastItem);

    protected abstract List<T> getContainer();

    protected abstract String getCaption(T item);

    protected abstract T newItem(final String value, final Path toAbsolutePath);

    protected abstract void addItem(final T item);

    private class Uploader implements Receiver {

        @Override
        public synchronized OutputStream receiveUpload(final String fileName, final String mimeType) {
            OutputStream fos = null;
            Path path = Paths.get("tmp", System.nanoTime() + fileName);
            try {
                Files.createFile(path);
                fos = Files.newOutputStream(path);
            } catch (IOException e) {
                Notification.show("Could not open file");
            }
            T item = newItem(captionAdd.getValue(), path.toAbsolutePath());
            addItem(item);
            return fos;
        }
    }
    public void refresh() {
        status.setText(data.toString());
        table.getDataProvider().refreshAll();
    }
    public HorizontalLayout getContent() {
        return this;
    }
}
