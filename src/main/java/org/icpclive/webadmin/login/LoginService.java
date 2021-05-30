package org.icpclive.webadmin.login;

import com.vaadin.flow.server.VaadinSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Aksenov239 on 14.11.2015.
 */
public class LoginService {
    private static final String USER_ATTRIBUTE = "user";
    private static final String DELIMITER = ":";
    private static final String USERS_FILE = "/users.data";
    private static final Map<String, String> USERS;
    private static LoginService instance;

    static {
        USERS = new HashMap<>();
        final Scanner usersCredentialsScanner = new Scanner(LoginService.class.getResourceAsStream(USERS_FILE));
        while (usersCredentialsScanner.hasNext()) {
            final String[] userCredentials = usersCredentialsScanner.nextLine().split(DELIMITER);
            USERS.put(userCredentials[0], userCredentials[1]);
        }
    }

    private LoginService() {

    }

    public static LoginService getInstance() {
        if (instance == null) {
            instance = new LoginService();
        }
        return instance;
    }

    public boolean isValidUser(final String username, final String password) {
        return USERS.containsKey(username) && USERS.get(username).equals(password);
    }

    public void logInUser(final String username) {
        VaadinSession.getCurrent().setAttribute(USER_ATTRIBUTE, username);
    }

    public void logOutUser() {
        VaadinSession.getCurrent().setAttribute(USER_ATTRIBUTE, null);
    }

    public boolean isLoggedIn() {
        return VaadinSession.getCurrent().getAttribute(USER_ATTRIBUTE) != null;
    }
}