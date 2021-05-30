package org.icpclive.webadmin.login;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.icpclive.webadmin.MainView;


@Route("login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    private final static String FIELD_WIDTH = "23rem";

    public LoginView() {
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        final TextField usernameField = new TextField("Username");
        usernameField.setWidth(FIELD_WIDTH);

        final PasswordField passwordField = new PasswordField("Password");
        passwordField.setWidth(FIELD_WIDTH);

        final Button logInButton = newLoginButton(usernameField, passwordField);
        logInButton.addClickShortcut(Key.ENTER);

        add(new H1("Welcome"), usernameField, passwordField, logInButton);
    }

    private Button newLoginButton(final TextField usernameField, final PasswordField passwordField) {
        return new Button("Login", event -> {

            final String username = usernameField.getValue();
            final String password = passwordField.getValue();
            final LoginService loginService = LoginService.getInstance();
            if (loginService.isValidUser(username, password)) {
                loginService.logInUser(username);
                UI.getCurrent().navigate(MainView.class);
            } else {
                Notification.show("Wrong credentials");
            }
        });
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        if (LoginService.getInstance().isLoggedIn()) {
            event.forwardTo(MainView.class);
        }
    }
}
