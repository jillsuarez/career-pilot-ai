package edu.farmingdale.careerpilot.frontend.view;

import edu.farmingdale.careerpilot.frontend.ApiClient;
import edu.farmingdale.careerpilot.frontend.navigation.Route;
import edu.farmingdale.careerpilot.frontend.service.UiTaskRunner;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class DashboardView extends PageView {

    public DashboardView(
            ApiClient apiClient,
            UiTaskRunner taskRunner,
            String userEmail,
            Consumer<Route> navigationHandler
    ) {
        super("Dashboard");

        Label welcome = new Label("Welcome back, " + displayName(userEmail));
        welcome.getStyleClass().add("dashboard-welcome");

        Label introduction = new Label("Here is an overview of your job-search workspace.");
        introduction.getStyleClass().add("muted-label");

        Label documentValue = new Label("—");
        documentValue.getStyleClass().add("metric-value");
        FlowPane metrics = new FlowPane(14, 14);
        metrics.getStyleClass().add("metric-grid");
        metrics.getChildren().addAll(
                metricCard("Applications", "—", "Tracker integration pending"),
                metricCard("Interviews", "—", "Tracker integration pending"),
                metricCard("Saved documents", documentValue, "Available from your workspace"),
                metricCard("Resume profile", "—", "Complete your profile to get started")
        );

        Label quickActionsTitle = sectionTitle("Quick actions");
        Button profileButton = actionButton("Update resume profile", Route.PROFILE, navigationHandler);
        Button generateButton = actionButton("Generate a document", Route.GENERATE, navigationHandler);
        Button documentsButton = actionButton("View saved documents", Route.DOCUMENTS, navigationHandler);

        profileButton.setTooltip(createTooltip("Open and edit your resume profile."));
        generateButton.setTooltip(createTooltip("Create a resume or cover letter."));
        documentsButton.setTooltip(createTooltip("Open your saved documents."));

        Button refreshButton = new Button("Refresh document count");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setTooltip(createTooltip("Update the number of saved documents."));
        refreshButton.setOnAction(
                event -> loadDocumentCount(apiClient, taskRunner, documentValue, refreshButton)
        );

        HBox quickActions = new HBox(
                10,
                profileButton,
                generateButton,
                documentsButton,
                refreshButton
        );

        quickActions.getStyleClass().add("quick-actions");

        Label recentTitle = sectionTitle("Recent applications");
        VBox emptyState = new VBox(6);
        emptyState.getStyleClass().add("empty-state");
        Label emptyHeading = new Label("No applications to display yet");
        emptyHeading.getStyleClass().add("empty-state-heading");
        Label emptyDescription = new Label("Applications will appear here when the tracker module is connected.");
        emptyDescription.getStyleClass().add("muted-label");
        emptyDescription.setWrapText(true);
        emptyState.getChildren().addAll(emptyHeading, emptyDescription);

        getChildren().addAll(
                welcome,
                introduction,
                metrics,
                quickActionsTitle,
                quickActions,
                recentTitle,
                emptyState
        );
    }

    private VBox metricCard(String title, String value, String description) {
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("metric-value");
        return metricCard(title, valueLabel, description);
    }

    private VBox metricCard(String title, Label valueLabel, String description) {
        VBox card = new VBox(6);
        card.getStyleClass().add("metric-card");
        card.setPrefWidth(205);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-title");
        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("metric-description");
        descriptionLabel.setWrapText(true);
        card.getChildren().addAll(titleLabel, valueLabel, descriptionLabel);
        return card;
    }

    private Button actionButton(String label, Route route, Consumer<Route> navigationHandler) {
        Button button = new Button(label);
        button.getStyleClass().add("secondary-button");
        button.setOnAction(event -> navigationHandler.accept(route));
        return button;
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    private void loadDocumentCount(
            ApiClient apiClient,
            UiTaskRunner taskRunner,
            Label documentValue,
            Button refreshButton
    ) {
        refreshButton.setDisable(true);
        documentValue.setText("…");
        taskRunner.setStatus("Loading saved documents...");
        taskRunner.run(
                () -> apiClient.getDocuments().size(),
                count -> {
                    documentValue.setText(String.valueOf(count));
                    refreshButton.setDisable(false);
                    taskRunner.setStatus("Dashboard updated.");
                },
                () -> {
                    documentValue.setText("—");
                    refreshButton.setDisable(false);
                }
        );
    }
    private Tooltip createTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.millis(200));
        return tooltip;
    }

    private String displayName(String email) {
        if (email == null || email.isBlank()) {
            return "there";
        }
        String localPart = email.split("@", 2)[0].replace('.', ' ').replace('_', ' ').trim();
        if (localPart.isEmpty()) {
            return "there";
        }
        return Character.toUpperCase(localPart.charAt(0)) + localPart.substring(1);
    }
}
