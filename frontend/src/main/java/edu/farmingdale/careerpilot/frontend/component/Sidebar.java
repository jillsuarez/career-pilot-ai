package edu.farmingdale.careerpilot.frontend.component;

import edu.farmingdale.careerpilot.frontend.navigation.Route;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class Sidebar extends VBox {

    private static final String SELECTED_STYLE_CLASS = "selected";

    private final Map<Route, Button> routeButtons = new EnumMap<>(Route.class);
    private final Label userEmailLabel = new Label();
    private final Button logoutButton = new Button("Log out");
    private Consumer<Route> navigationHandler = route -> { };
    private Runnable logoutHandler = () -> { };

    public Sidebar() {
        super(10);
        getStyleClass().add("nav");

        Label title = new Label("Career Pilot AI");
        title.getStyleClass().add("nav-title");
        getChildren().add(title);

        for (Route route : Route.values()) {
            Button button = createRouteButton(route);
            routeButtons.put(route, button);
            getChildren().add(button);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        userEmailLabel.getStyleClass().add("nav-user");
        userEmailLabel.setWrapText(true);

        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setOnAction(event -> logoutHandler.run());

        getChildren().addAll(spacer, userEmailLabel, logoutButton);
    }

    public void setOnNavigate(Consumer<Route> navigationHandler) {
        this.navigationHandler = navigationHandler == null ? route -> { } : navigationHandler;
    }

    public void setOnLogout(Runnable logoutHandler) {
        this.logoutHandler = logoutHandler == null ? () -> { } : logoutHandler;
    }

    public void setUserEmail(String email) {
        userEmailLabel.setText(email == null ? "" : email);
    }

    public void select(Route selectedRoute) {
        routeButtons.forEach((route, button) -> {
            if (route == selectedRoute) {
                if (!button.getStyleClass().contains(SELECTED_STYLE_CLASS)) {
                    button.getStyleClass().add(SELECTED_STYLE_CLASS);
                }
            } else {
                button.getStyleClass().remove(SELECTED_STYLE_CLASS);
            }
        });
    }

    private Button createRouteButton(Route route) {
        Button button = new Button(route.getLabel());
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> navigationHandler.accept(route));
        return button;
    }
}
