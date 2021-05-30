package org.icpclive.webadmin.creepingline;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.icpclive.webadmin.utils.RefreshableContent;

public class CreepingLineView  extends HorizontalLayout implements RefreshableContent<HorizontalLayout> {
    private final FactDemonstrator factDemonstrator;
    private final MessageForm messageForm;

    public  CreepingLineView() {
        factDemonstrator = new FactDemonstrator();
        messageForm = new MessageForm();
        VerticalLayout right = new VerticalLayout(factDemonstrator, messageForm);
        VerticalLayout left = new VerticalLayout(new CreepingLineDemonstrator(), new MessageFlowForm(factDemonstrator, messageForm));
        add(left, right);
    }

    @Override
    public HorizontalLayout getContent() {
        return this;
    }

    @Override
    public void refresh() {
        messageForm.refresh();
        factDemonstrator.refresh();
    }
}
