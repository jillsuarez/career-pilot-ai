package edu.farmingdale.careerpilot.frontend.component;

import edu.farmingdale.careerpilot.frontend.navigation.Route;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Sidebar extends VBox {

    private static final String SELECTED_STYLE_CLASS = "selected";

    private final Map<Route, Button> routeButtons = new EnumMap<>(Route.class);
    private final Label userEmailLabel = new Label();
    private final Button logoutButton = new Button("Log out");
    private final ToggleButton themeToggle = new ToggleButton("Light");
    private Consumer<Route> navigationHandler = route -> { };
    private Consumer<Boolean> themeHandler = darkMode -> { };
    private Runnable logoutHandler = () -> { };

    public Sidebar() {
        super(12);
        getStyleClass().add("nav");

        Label eyebrow = new Label("AI Workspace");
        eyebrow.getStyleClass().add("nav-eyebrow");

        Label title = new Label("Career Pilot");
        title.getStyleClass().add("nav-title");

        Label subtitle = new Label("Resume generation console");
        subtitle.getStyleClass().add("nav-subtitle");
        subtitle.setWrapText(true);

        VBox brand = new VBox(2, eyebrow, title, subtitle);
        brand.getStyleClass().add("nav-brand");
        getChildren().add(brand);

        for (Route route : Route.values()) {
            Button button = createRouteButton(route);
            routeButtons.put(route, button);
            getChildren().add(button);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label themeLabel = new Label("Theme");
        themeLabel.getStyleClass().add("nav-user");

        themeToggle.getStyleClass().add("theme-switch");
        themeToggle.setMaxWidth(Double.MAX_VALUE);
        themeToggle.setTooltip(createTooltip("Switch between light and dark mode."));
        themeToggle.selectedProperty().addListener((observable, oldValue, darkMode) -> {
            themeToggle.setText(darkMode ? "Dark" : "Light");
            themeHandler.accept(darkMode);
        });

        HBox themeRow = new HBox(10, themeLabel, themeToggle);
        themeRow.getStyleClass().add("theme-row");
        themeRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(themeToggle, Priority.ALWAYS);

        userEmailLabel.getStyleClass().add("nav-user");
        userEmailLabel.setWrapText(true);

        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setTooltip(createTooltip("Sign out of Career Pilot AI."));
        logoutButton.setOnAction(event -> logoutHandler.run());

        getChildren().addAll(spacer, themeRow, userEmailLabel, logoutButton);
    }

    public void setOnNavigate(Consumer<Route> navigationHandler) {
        this.navigationHandler = navigationHandler == null ? route -> { } : navigationHandler;
    }

    public void setOnThemeToggle(Consumer<Boolean> themeHandler) {
        this.themeHandler = themeHandler == null ? darkMode -> { } : themeHandler;
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
        button.setTooltip(createTooltip(getRouteTooltip(route)));
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> navigationHandler.accept(route));
        return button;
    }

    private Tooltip createTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.millis(200));
        return tooltip;
    }

    private String getRouteTooltip(Route route) {
        return switch (route) {
            case DASHBOARD -> "View your dashboard and quick actions.";
            case PROFILE -> "Edit your resume profile.";
            case GENERATE -> "Generate a resume or cover letter.";
            case DOCUMENTS -> "View your saved documents.";
        };
    }
}
