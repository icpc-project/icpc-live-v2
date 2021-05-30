package org.icpclive.webadmin;


import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("test")
public class TestView extends VerticalLayout {

    public TestView() {
        add(new Text("This is test"));
    }

}
