package edu.farmingdale.careerpilot.frontend.view;

import edu.farmingdale.careerpilot.frontend.ApiClient;
import edu.farmingdale.careerpilot.frontend.model.ResumeProfile;
import edu.farmingdale.careerpilot.frontend.navigation.Route;
import edu.farmingdale.careerpilot.frontend.service.UiTaskRunner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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

        Label introduction = new Label("Build a polished career packet from your profile, job brief, and saved AI drafts.");
        introduction.getStyleClass().add("muted-label");
        introduction.setWrapText(true);

        Label profileName = new Label("Loading profile...");
        profileName.getStyleClass().add("hero-profile-name");
        Label profileContact = new Label("Checking saved resume profile.");
        profileContact.getStyleClass().add("muted-label");
        profileContact.setWrapText(true);
        Label profileDetails = new Label(" ");
        profileDetails.getStyleClass().add("profile-snapshot-detail");
        profileDetails.setWrapText(true);

        VBox profileSnapshot = new VBox(
                12,
                pill("Profile signal"),
                profileName,
                profileContact,
                profileDetails,
                actionButton("Open Resume Profile", Route.PROFILE, navigationHandler, true)
        );
        profileSnapshot.getStyleClass().addAll("surface-panel", "dashboard-hero-panel");
        HBox.setHgrow(profileSnapshot, Priority.ALWAYS);

        Label documentValue = new Label("Loading...");
        documentValue.getStyleClass().add("metric-value");

        VBox documentPulse = new VBox(8, pill("Saved work"), documentValue, muted("Resumes and cover letters ready to revisit."));
        documentPulse.getStyleClass().addAll("surface-panel", "document-pulse");

        ProgressIndicator refreshIndicator = new ProgressIndicator();
        refreshIndicator.getStyleClass().add("small-progress");
        refreshIndicator.setMaxSize(18, 18);
        refreshIndicator.setVisible(false);
        refreshIndicator.setManaged(false);

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setTooltip(createTooltip("Reload profile and saved document information."));
        refreshButton.setOnAction(event -> loadDashboardData(
                apiClient,
                taskRunner,
                profileName,
                profileContact,
                profileDetails,
                documentValue,
                refreshButton,
                refreshIndicator
        ));

        HBox commandStrip = new HBox(18, profileSnapshot, documentPulse);
        commandStrip.getStyleClass().add("dashboard-command-strip");

        VBox resumeTile = launchTile(
                "Resume Builder",
                "Generate Resume",
                "Translate your profile into a role-specific resume draft.",
                Route.GENERATE,
                navigationHandler
        );
        VBox coverLetterTile = launchTile(
                "Cover Letter Lab",
                "Generate Cover Letter",
                "Shape a concise letter around the company and job brief.",
                Route.GENERATE,
                navigationHandler
        );
        VBox documentsTile = launchTile(
                "Document Vault",
                "Saved Documents",
                "Open the collection of generated drafts and saved edits.",
                Route.DOCUMENTS,
                navigationHandler
        );

        HBox launchPad = new HBox(14, resumeTile, coverLetterTile, documentsTile);
        launchPad.getStyleClass().add("launch-pad");

        HBox refreshRow = new HBox(10, refreshButton, refreshIndicator);
        refreshRow.getStyleClass().add("quick-actions");

        getChildren().addAll(welcome, introduction, commandStrip, launchPad, refreshRow);
        loadDashboardData(
                apiClient,
                taskRunner,
                profileName,
                profileContact,
                profileDetails,
                documentValue,
                refreshButton,
                refreshIndicator
        );
    }

    private VBox launchTile(
            String eyebrow,
            String title,
            String description,
            Route route,
            Consumer<Route> navigationHandler
    ) {
        Label eyebrowLabel = pill(eyebrow);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("launch-title");
        titleLabel.setWrapText(true);
        Label descriptionLabel = muted(description);
        Button button = actionButton("Open", route, navigationHandler, false);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox tile = new VBox(10, eyebrowLabel, titleLabel, descriptionLabel, spacer, button);
        tile.getStyleClass().addAll("surface-panel", "launch-tile");
        HBox.setHgrow(tile, Priority.ALWAYS);
        return tile;
    }

    private Button actionButton(String label, Route route, Consumer<Route> navigationHandler, boolean primary) {
        Button button = new Button(label);
        button.getStyleClass().add(primary ? "primary-button" : "secondary-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> navigationHandler.accept(route));
        return button;
    }

    private Label pill(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("surface-pill");
        return label;
    }

    private Label muted(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("muted-label");
        label.setWrapText(true);
        return label;
    }

    private void loadDashboardData(
            ApiClient apiClient,
            UiTaskRunner taskRunner,
            Label profileName,
            Label profileContact,
            Label profileDetails,
            Label documentValue,
            Button refreshButton,
            ProgressIndicator refreshIndicator
    ) {
        refreshButton.setDisable(true);
        refreshIndicator.setVisible(true);
        refreshIndicator.setManaged(true);
        profileName.setText("Loading profile...");
        profileContact.setText("Checking saved resume profile.");
        profileDetails.setText(" ");
        documentValue.setText("Loading...");
        taskRunner.setStatus("Refreshing dashboard...");
        AtomicInteger pendingRequests = new AtomicInteger(2);

        taskRunner.run(
                apiClient::getResumeProfile,
                profile -> {
                    updateProfileCard(profile, profileName, profileContact, profileDetails);
                    finishDashboardRefresh(refreshButton, refreshIndicator, pendingRequests);
                    taskRunner.setStatus("Dashboard profile loaded.");
                },
                () -> {
                    profileName.setText("Profile unavailable");
                    profileContact.setText("Check that the backend is running.");
                    profileDetails.setText(" ");
                    finishDashboardRefresh(refreshButton, refreshIndicator, pendingRequests);
                }
        );

        taskRunner.run(
                () -> apiClient.getDocuments().size(),
                count -> {
                    documentValue.setText(count + (count == 1 ? " document" : " documents"));
                    finishDashboardRefresh(refreshButton, refreshIndicator, pendingRequests);
                    taskRunner.setStatus("Dashboard documents loaded.");
                },
                () -> {
                    documentValue.setText("Documents unavailable");
                    finishDashboardRefresh(refreshButton, refreshIndicator, pendingRequests);
                }
        );
    }

    private void updateProfileCard(
            ResumeProfile profile,
            Label profileName,
            Label profileContact,
            Label profileDetails
    ) {
        if (profile == null || isBlank(profile.getFullName())) {
            profileName.setText("Profile not started");
            profileContact.setText("Add your name, education, and skills before generating documents.");
            profileDetails.setText(" ");
            return;
        }

        profileName.setText(profile.getFullName());
        profileContact.setText(joinNonBlank(profile.getEmail(), profile.getPhone(), "No contact details saved"));
        profileDetails.setText(summaryLine(profile));
    }

    private String summaryLine(ResumeProfile profile) {
        String skills = shortValue(profile.getSkills(), 92);
        String education = shortValue(profile.getEducation(), 92);
        if (!isBlank(skills) && !isBlank(education)) {
            return "Skills: " + skills + "\nEducation: " + education;
        }
        if (!isBlank(skills)) {
            return "Skills: " + skills;
        }
        if (!isBlank(education)) {
            return "Education: " + education;
        }
        return "Add education and skills to complete your profile.";
    }

    private String joinNonBlank(String first, String second, String fallback) {
        boolean hasFirst = !isBlank(first);
        boolean hasSecond = !isBlank(second);
        if (hasFirst && hasSecond) {
            return first.trim() + " | " + second.trim();
        }
        if (hasFirst) {
            return first.trim();
        }
        if (hasSecond) {
            return second.trim();
        }
        return fallback;
    }

    private String shortValue(String value, int maxLength) {
        if (isBlank(value)) {
            return "";
        }
        String normalized = value.trim().replaceAll("\s+", " ");
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        return normalized.substring(0, maxLength - 3) + "...";
    }

    private void finishDashboardRefresh(
            Button refreshButton,
            ProgressIndicator refreshIndicator,
            AtomicInteger pendingRequests
    ) {
        if (pendingRequests.decrementAndGet() > 0) {
            return;
        }
        refreshButton.setDisable(false);
        refreshIndicator.setVisible(false);
        refreshIndicator.setManaged(false);
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
