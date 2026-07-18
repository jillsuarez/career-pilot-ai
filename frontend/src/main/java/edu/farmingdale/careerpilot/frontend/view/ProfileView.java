package edu.farmingdale.careerpilot.frontend.view;

import edu.farmingdale.careerpilot.frontend.ApiClient;
import edu.farmingdale.careerpilot.frontend.model.ResumeProfile;
import edu.farmingdale.careerpilot.frontend.service.UiTaskRunner;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ProfileView extends PageView {

    public ProfileView(ApiClient apiClient, UiTaskRunner taskRunner) {
        super("Resume Profile");

        TextField fullNameField = createTextField();
        TextField emailField = createTextField();
        TextField phoneField = createTextField();
        TextArea educationArea = createTextArea();
        TextArea skillsArea = createTextArea();
        TextArea experienceArea = createTextArea();
        educationArea.setPrefRowCount(8);
        skillsArea.setPrefRowCount(8);
        experienceArea.setPrefRowCount(8);

        GridPane identityForm = new GridPane();
        identityForm.setHgap(12);
        identityForm.setVgap(12);
        identityForm.getStyleClass().add("form-grid");
        addFormRow(identityForm, 0, "Full name", fullNameField);
        addFormRow(identityForm, 1, "Email", emailField);
        addFormRow(identityForm, 2, "Phone", phoneField);

        VBox identityPanel = new VBox(14, panelHeader("Identity", "The contact layer used across generated documents."), identityForm);
        identityPanel.getStyleClass().addAll("surface-panel", "profile-identity-panel");

        GridPane storyForm = new GridPane();
        storyForm.setHgap(12);
        storyForm.setVgap(12);
        storyForm.getStyleClass().add("form-grid");
        addFormRow(storyForm, 0, "Education", educationArea);
        addFormRow(storyForm, 1, "Skills", skillsArea);
        addFormRow(storyForm, 2, "Experience", experienceArea);

        VBox storyPanel = new VBox(14, panelHeader("Career Signal", "The resume evidence AI uses to write stronger drafts."), storyForm);
        storyPanel.getStyleClass().addAll("surface-panel", "profile-story-panel");
        HBox.setHgrow(storyPanel, Priority.ALWAYS);

        Button saveButton = new Button("Save Profile");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setTooltip(createTooltip("Save your resume profile information."));

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.getStyleClass().add("small-progress");
        progressIndicator.setMaxSize(18, 18);
        progressIndicator.setVisible(false);
        progressIndicator.setManaged(false);

        HBox layout = new HBox(16, identityPanel, storyPanel);
        layout.getStyleClass().add("profile-workbench");

        HBox actions = new HBox(10, saveButton, progressIndicator);
        actions.getStyleClass().add("floating-actions");

        saveButton.setOnAction(event -> saveProfile(
                apiClient,
                taskRunner,
                saveButton,
                progressIndicator,
                fullNameField,
                emailField,
                phoneField,
                educationArea,
                skillsArea,
                experienceArea
        ));

        getChildren().addAll(layout, actions);
        loadProfile(
                apiClient,
                taskRunner,
                saveButton,
                progressIndicator,
                fullNameField,
                emailField,
                phoneField,
                educationArea,
                skillsArea,
                experienceArea
        );
    }

    private VBox panelHeader(String title, String subtitle) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("panel-title");
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("muted-label");
        subtitleLabel.setWrapText(true);
        return new VBox(3, titleLabel, subtitleLabel);
    }

    private TextField createTextField() {
        TextField textField = new TextField();
        textField.getStyleClass().add("form-control");
        return textField;
    }

    private void addFormRow(GridPane form, int row, String label, Node field) {
        form.add(fieldLabel(label), 0, row);
        form.add(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
    }

    private void loadProfile(
            ApiClient apiClient,
            UiTaskRunner taskRunner,
            Button saveButton,
            ProgressIndicator progressIndicator,
            TextField fullNameField,
            TextField emailField,
            TextField phoneField,
            TextArea educationArea,
            TextArea skillsArea,
            TextArea experienceArea
    ) {
        setLoading(saveButton, progressIndicator, true);
        taskRunner.setStatus("Loading profile...");
        taskRunner.run(apiClient::getResumeProfile, profile -> {
            fullNameField.setText(valueOrEmpty(profile.getFullName()));
            emailField.setText(valueOrEmpty(profile.getEmail()));
            phoneField.setText(valueOrEmpty(profile.getPhone()));
            educationArea.setText(valueOrEmpty(profile.getEducation()));
            skillsArea.setText(valueOrEmpty(profile.getSkills()));
            experienceArea.setText(valueOrEmpty(profile.getExperience()));
            setLoading(saveButton, progressIndicator, false);
            taskRunner.setStatus("Profile loaded.");
        }, () -> setLoading(saveButton, progressIndicator, false));
    }

    private void saveProfile(
            ApiClient apiClient,
            UiTaskRunner taskRunner,
            Button saveButton,
            ProgressIndicator progressIndicator,
            TextField fullNameField,
            TextField emailField,
            TextField phoneField,
            TextArea educationArea,
            TextArea skillsArea,
            TextArea experienceArea
    ) {
        if (isBlank(fullNameField.getText()) || isBlank(educationArea.getText()) || isBlank(skillsArea.getText())) {
            taskRunner.setStatus("Full name, education, and skills are required before saving.");
            return;
        }

        ResumeProfile profile = new ResumeProfile();
        profile.setFullName(fullNameField.getText().trim());
        profile.setEmail(valueOrEmpty(emailField.getText()).trim());
        profile.setPhone(valueOrEmpty(phoneField.getText()).trim());
        profile.setEducation(educationArea.getText().trim());
        profile.setSkills(skillsArea.getText().trim());
        profile.setExperience(valueOrEmpty(experienceArea.getText()).trim());

        setLoading(saveButton, progressIndicator, true);
        taskRunner.setStatus("Saving profile...");
        taskRunner.run(() -> apiClient.saveResumeProfile(profile), saved -> {
            setLoading(saveButton, progressIndicator, false);
            taskRunner.setStatus("Profile saved.");
        }, () -> setLoading(saveButton, progressIndicator, false));
    }

    private void setLoading(Button saveButton, ProgressIndicator progressIndicator, boolean loading) {
        saveButton.setDisable(loading);
        progressIndicator.setVisible(loading);
        progressIndicator.setManaged(loading);
    }

    private Tooltip createTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.millis(200));
        return tooltip;
    }
}
