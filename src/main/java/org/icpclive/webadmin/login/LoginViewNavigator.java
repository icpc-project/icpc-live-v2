package org.icpclive.webadmin.login;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

public class LoginViewNavigator implements VaadinServiceInitListener {

    @Override
    public void serviceInit(final ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
           final UI ui = uiEvent.getUI();
           ui.addBeforeEnterListener(this::processAuthenticateNavigation);
        });
    }

    private void processAuthenticateNavigation(final BeforeEnterEvent event) {
        boolean loginPage = LoginView.class.equals(event.getNavigationTarget());
        if (!loginPage && !LoginService.getInstance().isLoggedIn()) {
            event.forwardTo(LoginView.class);
        }
    }
}
