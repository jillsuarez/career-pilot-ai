package edu.farmingdale.careerpilot.frontend.component;

import edu.farmingdale.careerpilot.frontend.navigation.Route;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class AppShell extends BorderPane {

    private final Sidebar sidebar = new Sidebar();
    private final Label statusLabel = new Label("Start the backend before using the app.");

    public AppShell() {
        getStyleClass().add("app-shell");
        statusLabel.getStyleClass().add("status-label");
        setLeft(sidebar);
        setBottom(statusLabel);
    }

    public void setOnNavigate(Consumer<Route> navigationHandler) {
        sidebar.setOnNavigate(navigationHandler);
    }

    public void setOnLogout(Runnable logoutHandler) {
        sidebar.setOnLogout(logoutHandler);
    }

    public void setUserEmail(String email) {
        sidebar.setUserEmail(email);
    }

    public void show(Route route, Node view) {
        sidebar.select(route);
        setCenter(view);
    }

    public void setStatus(String message) {
        if (Platform.isFxApplicationThread()) {
            statusLabel.setText(message);
        } else {
            Platform.runLater(() -> statusLabel.setText(message));
        }
    }
}
