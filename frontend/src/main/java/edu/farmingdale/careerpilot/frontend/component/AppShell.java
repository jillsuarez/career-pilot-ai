package edu.farmingdale.careerpilot.frontend.component;

import edu.farmingdale.careerpilot.frontend.navigation.Route;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public class AppShell extends BorderPane {

    private static final String LIGHT_THEME = "theme-light";
    private static final String DARK_THEME = "theme-dark";

    private final Sidebar sidebar = new Sidebar();
    private final Label activeRouteLabel = new Label("Dashboard");
    private final Label routeContextLabel = new Label("Your AI workspace is ready.");
    private final Label backendPill = new Label("API localhost:8080");
    private final Label statusLabel = new Label("Start the backend before using the app.");

    public AppShell() {
        getStyleClass().addAll("app-shell", LIGHT_THEME);
        sidebar.setOnThemeToggle(this::setDarkMode);
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setWrapText(true);
        setLeft(sidebar);
        setTop(createTopBar());
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
        activeRouteLabel.setText(route.getLabel());
        routeContextLabel.setText(routeContext(route));
        setCenter(view);
    }

    public void setStatus(String message) {
        if (Platform.isFxApplicationThread()) {
            statusLabel.setText(message);
        } else {
            Platform.runLater(() -> statusLabel.setText(message));
        }
    }

    public void showError(String message) {
        Runnable action = () -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Career Pilot AI");
            alert.setHeaderText("Something went wrong");
            alert.setContentText(message == null || message.isBlank() ? "Please try again." : message);
            Window owner = getScene() == null ? null : getScene().getWindow();
            if (owner != null) {
                alert.initOwner(owner);
            }
            alert.showAndWait();
        };
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

    private HBox createTopBar() {
        Label eyebrow = new Label("Career Pilot AI");
        eyebrow.getStyleClass().add("topbar-eyebrow");
        activeRouteLabel.getStyleClass().add("topbar-title");
        routeContextLabel.getStyleClass().add("topbar-context");
        routeContextLabel.setWrapText(true);

        VBox titleStack = new VBox(2, eyebrow, activeRouteLabel, routeContextLabel);
        titleStack.getStyleClass().add("topbar-title-stack");

        backendPill.getStyleClass().add("topbar-pill");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBar = new HBox(16, titleStack, spacer, backendPill);
        topBar.getStyleClass().add("workspace-topbar");
        topBar.setAlignment(Pos.CENTER_LEFT);
        return topBar;
    }

    private String routeContext(Route route) {
        return switch (route) {
            case DASHBOARD -> "Launch profile, generation, and saved work from one command surface.";
            case PROFILE -> "Tune the source profile that powers each generated document.";
            case GENERATE -> "Convert a job brief into tailored resume and cover letter drafts.";
            case DOCUMENTS -> "Review and reuse every saved AI-generated document.";
        };
    }

    private void setDarkMode(boolean darkMode) {
        getStyleClass().removeAll(LIGHT_THEME, DARK_THEME);
        getStyleClass().add(darkMode ? DARK_THEME : LIGHT_THEME);
    }
}
