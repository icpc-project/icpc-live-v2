package org.icpclive.webadmin.creepingline;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import org.icpclive.webadmin.mainscreen.loaders.TwitterLoader;
import org.icpclive.webadmin.utils.Utils;
import twitter4j.TwitterException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class MessageFlowForm extends VerticalLayout {
    private final Grid<Message> table;
    private final TextField loadField;
    private final TextField filterSource;
    private final Button loadButton;

    public MessageFlowForm(FactDemonstrator factDemonstrator, MessageForm messageForm) {
        final MessageService service = MessageService.getInstance();
        List<Message> container = service.getMessageFlowContainer();
//        container.add(new Message("a", 0, Long.MAX_VALUE, true, "a"));
//        container.add(new Message("b", 0, Long.MAX_VALUE, true, "b"));
//        container.add(new Message("c", 0, Long.MAX_VALUE, true, "c"));
//        container.add(new Message("ab", 0, Long.MAX_VALUE, true, "ab"));

        table = new Grid<>();
        ListDataProvider<Message> dataProvider = new ListDataProvider<>(container);
        table.setItems(container);
        table.setDataProvider(dataProvider);
        table.addColumn(Message::getSource).setHeader("Source").setFlexGrow(1);
        table.addColumn(Message::getMessage).setHeader("Message").setFlexGrow(3);
        table.asSingleSelect().addValueChangeListener(event -> {
           if (event.getValue() != null) {
               Message message = event.getValue();
               factDemonstrator.getTitle().setValue(message.getSource());
               factDemonstrator.getText().setValue(message.getMessage());
               messageForm.editOutside(message);
           }
        });
        table.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        table.setHeightByRows(true);

        filterSource = new TextField("Filter source");
        filterSource.addValueChangeListener(event -> {
            dataProvider.setFilter(message -> message.getSource().startsWith(event.getValue()));
            dataProvider.refreshAll();
        });

        loadField = new TextField("Load field");
        loadButton = new Button("Load");
        loadButton.addClickListener(event ->  {
           String text = loadField.getValue();
            Arrays.stream(text.split(",")).flatMap(query -> {
                try {
                    return TwitterLoader.getInstance().loadByQuery(query).stream();
                } catch (TwitterException e) {
                    Notification.show("Twitter exception: " + e.getMessage());
                    return Stream.empty();
                }
            }).forEach(MessageService::processTwitterMessage);
        });

        FormLayout controller = new FormLayout(new FormLayout(loadField, loadButton), filterSource);
        controller.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        add(controller, table);
    }


}
