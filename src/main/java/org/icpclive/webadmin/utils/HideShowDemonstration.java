package org.icpclive.webadmin.utils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.icpclive.webadmin.mainscreen.MainScreenService;

public abstract class HideShowDemonstration extends VerticalLayout implements Refreshable {
    private final String shown;
    private final String notShown;
    private final Label status;

    public HideShowDemonstration(String name) {
        shown = name + " is shown";
        notShown = name + " is not shown";
        status = new Label(getStatus());
        status.getElement().getStyle().set("font-size", "large");

        final Button onButton = newButton("Show " + name, true, shown);
        final Button offButton = newButton("Hide " + name, false, notShown);
        FormLayout clockManager = new FormLayout(onButton, offButton);
        clockManager.setWidth("33%");
        add(status, clockManager);
    }

    protected abstract boolean getVisibleStatus();

    protected abstract void setVisibility(boolean visibility);

    public String getStatus() {
        if (getVisibleStatus()) {
            return shown;
        }
        return notShown;
    }

    private Button newButton(String name, boolean visibility, String statusText) {
        Button clockButton = new Button(name);
        clockButton.addClickListener(event -> {
            setVisibility(visibility);
            status.setText(statusText);
        });
        return clockButton;
    }
    public void refresh() {
        status.setText(getStatus());
    }
}
