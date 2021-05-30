package org.icpclive.webadmin.creepingline;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.utils.Refreshable;


public class FactDemonstrator extends FormLayout implements Refreshable {
    private final Label status;
    private final TextField text;
    private final TextField title;

    public FactDemonstrator() {
         status = new Label(getStatus());
        text = new TextField("Text");
        title = new TextField("Tittle");

        Button show = new Button("Show fact");
        show.addClickListener(event -> {
           String outcome =  MainScreenService.getInstance().getFactData().show(title.getValue(), text.getValue());
           if (outcome != null) {
               Notification.show(outcome);
           }
           status.setText(getStatus());
        });

        Button hide = new Button("Hide fact");
        hide.addClickListener(event -> {
            MainScreenService.getInstance().getFactData().hide();
            status.setText(getStatus());
        });
        FormLayout controllers = new FormLayout(show, hide);
        setResponsiveSteps(new ResponsiveStep("0", 1));
        add(status, text, title, controllers);
    }

    private String getStatus() {
        return MainScreenService.getInstance().getFactData().toString();
    }

    public TextField getText() {
        return text;
    }

    public TextField getTitle() {
        return title;
    }

    @Override
    public void refresh() {
        status.setText(getStatus());
    }
}
