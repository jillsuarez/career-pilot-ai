package edu.farmingdale.careerpilot.frontend.view;

import edu.farmingdale.careerpilot.frontend.ApiClient;
import edu.farmingdale.careerpilot.frontend.model.GenerateRequest;
import edu.farmingdale.careerpilot.frontend.model.GeneratedDocument;
import edu.farmingdale.careerpilot.frontend.model.GenerationResponse;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class GenerateView extends PageView {

    private final ApiClient apiClient;
    private final UiTaskRunner taskRunner;
    private final TextArea outputArea = createTextArea();
    private GenerateRequest lastGenerateRequest;
    private String currentDocumentType;

    public GenerateView(ApiClient apiClient, UiTaskRunner taskRunner) {
        super("Generate Documents");
        this.apiClient = apiClient;
        this.taskRunner = taskRunner;

        TextField companyField = createTextField();
        TextField jobTitleField = createTextField();
        TextArea jobDescriptionArea = createTextArea();
        jobDescriptionArea.setPrefRowCount(16);
        outputArea.setPrefRowCount(22);
        outputArea.setPromptText("Generated content appears here. Review and edit before saving.");

        GridPane briefForm = new GridPane();
        briefForm.setHgap(12);
        briefForm.setVgap(12);
        briefForm.getStyleClass().add("form-grid");
        addFormRow(briefForm, 0, "Company", companyField);
        addFormRow(briefForm, 1, "Job title", jobTitleField);
        addFormRow(briefForm, 2, "Job brief", jobDescriptionArea);

        Button resumeButton = new Button("Resume");
        resumeButton.getStyleClass().add("primary-button");
        resumeButton.setTooltip(createTooltip("Generate a resume using the job information entered above."));

        Button coverLetterButton = new Button("Cover Letter");
        coverLetterButton.getStyleClass().add("secondary-button");
        coverLetterButton.setTooltip(createTooltip("Generate a cover letter using the job information entered above."));

        Button saveButton = new Button("Save Draft");
        saveButton.getStyleClass().add("secondary-button");
        saveButton.setDisable(true);
        saveButton.setTooltip(createTooltip("Save the edited generated document."));

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.getStyleClass().add("small-progress");
        progressIndicator.setMaxSize(18, 18);
        progressIndicator.setVisible(false);
        progressIndicator.setManaged(false);

        Label progressLabel = new Label("Generating...");
        progressLabel.getStyleClass().add("muted-label");
        progressLabel.setVisible(false);
        progressLabel.setManaged(false);

        HBox generateActions = new HBox(10, resumeButton, coverLetterButton, progressIndicator, progressLabel);
        generateActions.getStyleClass().add("quick-actions");

        VBox briefPanel = new VBox(
                14,
                panelHeader("Job Brief Studio", "Drop in the role context and choose the output type."),
                briefForm,
                generateActions
        );
        briefPanel.getStyleClass().addAll("surface-panel", "brief-panel");

        Label outputTitle = new Label("Editable AI Draft");
        outputTitle.getStyleClass().add("panel-title");
        Region outputSpacer = new Region();
        HBox.setHgrow(outputSpacer, Priority.ALWAYS);
        HBox outputHeader = new HBox(12, outputTitle, outputSpacer, saveButton);
        outputHeader.getStyleClass().add("output-header");

        VBox outputPanel = new VBox(12, outputHeader, outputArea);
        outputPanel.getStyleClass().addAll("surface-panel", "output-panel");
        HBox.setHgrow(outputPanel, Priority.ALWAYS);
        VBox.setVgrow(outputArea, Priority.ALWAYS);

        HBox studio = new HBox(16, briefPanel, outputPanel);
        studio.getStyleClass().add("generator-studio");
        VBox.setVgrow(studio, Priority.ALWAYS);

        resumeButton.setOnAction(event -> generateDocument(
                "resume",
                companyField,
                jobTitleField,
                jobDescriptionArea,
                resumeButton,
                coverLetterButton,
                saveButton,
                progressIndicator,
                progressLabel
        ));
        coverLetterButton.setOnAction(event -> generateDocument(
                "cover letter",
                companyField,
                jobTitleField,
                jobDescriptionArea,
                resumeButton,
                coverLetterButton,
                saveButton,
                progressIndicator,
                progressLabel
        ));
        saveButton.setOnAction(event -> saveGeneratedOutput(saveButton, progressIndicator, progressLabel));
        outputArea.textProperty().addListener((observable, oldValue, newValue) -> updateSaveButton(saveButton));

        getChildren().add(studio);
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

    private void generateDocument(
            String type,
            TextField companyField,
            TextField jobTitleField,
            TextArea jobDescriptionArea,
            Button resumeButton,
            Button coverLetterButton,
            Button saveButton,
            ProgressIndicator progressIndicator,
            Label progressLabel
    ) {
        if (isBlank(jobDescriptionArea.getText())) {
            taskRunner.setStatus("Job description is required before generating.");
            return;
        }

        GenerateRequest request = new GenerateRequest();
        request.setCompany(valueOrEmpty(companyField.getText()).trim());
        request.setJobTitle(valueOrEmpty(jobTitleField.getText()).trim());
        request.setJobDescription(jobDescriptionArea.getText().trim());

        lastGenerateRequest = request;
        currentDocumentType = null;
        outputArea.clear();
        setGenerationLoading(resumeButton, coverLetterButton, saveButton, progressIndicator, progressLabel, true);
        taskRunner.setStatus("Generating " + type + "...");

        if ("resume".equals(type)) {
            taskRunner.run(() -> apiClient.generateResume(request), response -> showGeneratedText(
                    response,
                    resumeButton,
                    coverLetterButton,
                    saveButton,
                    progressIndicator,
                    progressLabel
            ), () -> setGenerationLoading(resumeButton, coverLetterButton, saveButton, progressIndicator, progressLabel, false));
        } else {
            taskRunner.run(() -> apiClient.generateCoverLetter(request), response -> showGeneratedText(
                    response,
                    resumeButton,
                    coverLetterButton,
                    saveButton,
                    progressIndicator,
                    progressLabel
            ), () -> setGenerationLoading(resumeButton, coverLetterButton, saveButton, progressIndicator, progressLabel, false));
        }
    }

    private void showGeneratedText(
            GenerationResponse response,
            Button resumeButton,
            Button coverLetterButton,
            Button saveButton,
            ProgressIndicator progressIndicator,
            Label progressLabel
    ) {
        currentDocumentType = isBlank(response.getDocumentType()) ? "document" : response.getDocumentType();
        outputArea.setText(valueOrEmpty(response.getContent()));
        setGenerationLoading(resumeButton, coverLetterButton, saveButton, progressIndicator, progressLabel, false);
        updateSaveButton(saveButton);
        taskRunner.setStatus("Generated " + currentDocumentType + ". Review it before saving.");
    }

    private void saveGeneratedOutput(Button saveButton, ProgressIndicator progressIndicator, Label progressLabel) {
        if (lastGenerateRequest == null || isBlank(currentDocumentType)) {
            taskRunner.setStatus("Generate a document before saving.");
            return;
        }
        if (isBlank(outputArea.getText())) {
            taskRunner.setStatus("Generated content is required before saving.");
            return;
        }

        GeneratedDocument document = new GeneratedDocument();
        document.setDocumentType(currentDocumentType);
        document.setCompany(lastGenerateRequest.getCompany());
        document.setJobTitle(lastGenerateRequest.getJobTitle());
        document.setJobDescription(lastGenerateRequest.getJobDescription());
        document.setContent(outputArea.getText().trim());

        saveButton.setDisable(true);
        progressIndicator.setVisible(true);
        progressIndicator.setManaged(true);
        progressLabel.setText("Saving...");
        progressLabel.setVisible(true);
        progressLabel.setManaged(true);
        taskRunner.setStatus("Saving document...");
        taskRunner.run(() -> apiClient.saveDocument(document), saved -> {
            progressIndicator.setVisible(false);
            progressIndicator.setManaged(false);
            progressLabel.setVisible(false);
            progressLabel.setManaged(false);
            updateSaveButton(saveButton);
            taskRunner.setStatus("Document saved.");
        }, () -> {
            progressIndicator.setVisible(false);
            progressIndicator.setManaged(false);
            progressLabel.setVisible(false);
            progressLabel.setManaged(false);
            updateSaveButton(saveButton);
        });
    }

    private void setGenerationLoading(
            Button resumeButton,
            Button coverLetterButton,
            Button saveButton,
            ProgressIndicator progressIndicator,
            Label progressLabel,
            boolean loading
    ) {
        resumeButton.setDisable(loading);
        coverLetterButton.setDisable(loading);
        progressIndicator.setVisible(loading);
        progressIndicator.setManaged(loading);
        progressLabel.setText("Generating...");
        progressLabel.setVisible(loading);
        progressLabel.setManaged(loading);
        if (loading) {
            saveButton.setDisable(true);
        } else {
            updateSaveButton(saveButton);
        }
    }

    private void updateSaveButton(Button saveButton) {
        saveButton.setDisable(isBlank(currentDocumentType) || isBlank(outputArea.getText()));
    }

    private Tooltip createTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.millis(200));
        return tooltip;
    }
}
