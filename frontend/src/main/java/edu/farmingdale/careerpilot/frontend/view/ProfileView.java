package edu.farmingdale.careerpilot.frontend.view;

import edu.farmingdale.careerpilot.frontend.ApiClient;
import edu.farmingdale.careerpilot.frontend.model.ResumeProfile;
import edu.farmingdale.careerpilot.frontend.service.UiTaskRunner;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ProfileView extends PageView {

    public ProfileView(ApiClient apiClient, UiTaskRunner taskRunner) {
        super("Resume Profile");

        TextField fullNameField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextArea educationArea = createTextArea();
        TextArea skillsArea = createTextArea();
        TextArea experienceArea = createTextArea();

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        addFormRow(form, 0, "Full name", fullNameField);
        addFormRow(form, 1, "Email", emailField);
        addFormRow(form, 2, "Phone", phoneField);
        addFormRow(form, 3, "Education", educationArea);
        addFormRow(form, 4, "Skills", skillsArea);
        addFormRow(form, 5, "Experience", experienceArea);

        Button saveButton = new Button("Save Profile");

        Tooltip saveTooltip = new Tooltip("Save your resume profile information.");
        saveTooltip.setShowDelay(Duration.millis(200));
        saveButton.setTooltip(saveTooltip);

        saveButton.setOnAction(event -> {
            if (isBlank(fullNameField.getText()) || isBlank(educationArea.getText()) || isBlank(skillsArea.getText())) {
                taskRunner.setStatus("Full name, education, and skills are required.");
                return;
            }

            ResumeProfile profile = new ResumeProfile();
            profile.setFullName(fullNameField.getText());
            profile.setEmail(emailField.getText());
            profile.setPhone(phoneField.getText());
            profile.setEducation(educationArea.getText());
            profile.setSkills(skillsArea.getText());
            profile.setExperience(experienceArea.getText());
            taskRunner.run(() -> apiClient.saveResumeProfile(profile), saved -> taskRunner.setStatus("Profile saved."));
        });

        getChildren().addAll(form, saveButton);

        taskRunner.run(apiClient::getResumeProfile, profile -> {
            fullNameField.setText(valueOrEmpty(profile.getFullName()));
            emailField.setText(valueOrEmpty(profile.getEmail()));
            phoneField.setText(valueOrEmpty(profile.getPhone()));
            educationArea.setText(valueOrEmpty(profile.getEducation()));
            skillsArea.setText(valueOrEmpty(profile.getSkills()));
            experienceArea.setText(valueOrEmpty(profile.getExperience()));
            taskRunner.setStatus("Profile loaded.");
        });
    }

    private void addFormRow(GridPane form, int row, String label, Node field) {
        form.add(new Label(label), 0, row);
        form.add(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
    }
}
