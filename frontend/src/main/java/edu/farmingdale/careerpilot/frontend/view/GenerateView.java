package edu.farmingdale.careerpilot.frontend.view;

import edu.farmingdale.careerpilot.frontend.ApiClient;
import edu.farmingdale.careerpilot.frontend.model.GenerateRequest;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import edu.farmingdale.careerpilot.frontend.model.GeneratedDocument;
import edu.farmingdale.careerpilot.frontend.model.GenerationResponse;
import edu.farmingdale.careerpilot.frontend.service.UiTaskRunner;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

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

        TextField companyField = new TextField();
        TextField jobTitleField = new TextField();
        TextArea jobDescriptionArea = createTextArea();
        outputArea.setPromptText(
                "Your generated resume or cover letter will appear here.\n\n" +
                        "You can review and edit it before saving."
        );

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        addFormRow(form, 0, "Company", companyField);
        addFormRow(form, 1, "Job title", jobTitleField);
        addFormRow(form, 2, "Job description", jobDescriptionArea);

        Button resumeButton = new Button("Generate Resume");
        Tooltip resumeTooltip = new Tooltip("Generate a resume using the job information entered above.");
        resumeTooltip.setShowDelay(Duration.millis(200));
        resumeButton.setTooltip(resumeTooltip);
        resumeButton.setOnAction(
                event -> generateDocument("resume", companyField, jobTitleField, jobDescriptionArea)
        );

        Button coverLetterButton = new Button("Generate Cover Letter");
        Tooltip coverLetterTooltip = new Tooltip("Generate a cover letter using the job information entered above.");
        coverLetterTooltip.setShowDelay(Duration.millis(200));
        coverLetterButton.setTooltip(coverLetterTooltip);
        coverLetterButton.setOnAction(
                event -> generateDocument("cover letter", companyField, jobTitleField, jobDescriptionArea)
        );

        Button saveButton = new Button("Save Edited Output");
        Tooltip saveTooltip = new Tooltip("Save the generated document after reviewing or editing it.");
        saveTooltip.setShowDelay(Duration.millis(200));
        saveButton.setTooltip(saveTooltip);
        saveButton.setOnAction(event -> saveGeneratedOutput());

        getChildren().addAll(
                form,
                new HBox(10, resumeButton, coverLetterButton, saveButton),
                new Label("Editable output"),
                outputArea
        );
    }

    private void addFormRow(GridPane form, int row, String label, Node field) {
        form.add(new Label(label), 0, row);
        form.add(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
    }

    private void generateDocument(String type, TextField companyField, TextField jobTitleField, TextArea jobDescriptionArea) {
        if (isBlank(jobDescriptionArea.getText())) {
            taskRunner.setStatus("Job description is required.");
            return;
        }

        GenerateRequest request = new GenerateRequest();
        request.setCompany(companyField.getText());
        request.setJobTitle(jobTitleField.getText());
        request.setJobDescription(jobDescriptionArea.getText());
        lastGenerateRequest = request;
        currentDocumentType = type;
        outputArea.clear();
        taskRunner.setStatus("Generating " + type + "...");

        if ("resume".equals(type)) {
            taskRunner.run(() -> apiClient.generateResume(request), this::showGeneratedText);
        } else {
            taskRunner.run(() -> apiClient.generateCoverLetter(request), this::showGeneratedText);
        }
    }

    private void showGeneratedText(GenerationResponse response) {
        currentDocumentType = response.getDocumentType();
        outputArea.setText(valueOrEmpty(response.getContent()));
        taskRunner.setStatus("Generated " + currentDocumentType + ". Edit it before saving.");
    }

    private void saveGeneratedOutput() {
        if (lastGenerateRequest == null || isBlank(currentDocumentType)) {
            taskRunner.setStatus("Generate a document before saving.");
            return;
        }
        if (isBlank(outputArea.getText())) {
            taskRunner.setStatus("Generated content is required.");
            return;
        }

        GeneratedDocument document = new GeneratedDocument();
        document.setDocumentType(currentDocumentType);
        document.setCompany(lastGenerateRequest.getCompany());
        document.setJobTitle(lastGenerateRequest.getJobTitle());
        document.setJobDescription(lastGenerateRequest.getJobDescription());
        document.setContent(outputArea.getText());

        taskRunner.run(() -> apiClient.saveDocument(document), saved -> taskRunner.setStatus("Document saved."));
    }
}
