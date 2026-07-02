package edu.farmingdale.careerpilot.frontend;

import edu.farmingdale.careerpilot.frontend.model.GenerateRequest;
import edu.farmingdale.careerpilot.frontend.model.GeneratedDocument;
import edu.farmingdale.careerpilot.frontend.model.GenerationResponse;
import edu.farmingdale.careerpilot.frontend.model.ResumeProfile;
import java.util.List;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CareerPilotApp extends Application {

    private final ApiClient apiClient = new ApiClient();
    private BorderPane root;
    private Label statusLabel;
    private TextArea outputArea;
    private GenerateRequest lastGenerateRequest;
    private String currentDocumentType;

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        root.setLeft(createNavigation());

        statusLabel = new Label("Start the backend before using the app.");
        statusLabel.getStyleClass().add("status-label");
        root.setBottom(statusLabel);

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("Career Pilot AI Lite");
        stage.setScene(scene);
        stage.show();

        showDashboard();
    }

    private VBox createNavigation() {
        VBox nav = new VBox(10);
        nav.getStyleClass().add("nav");

        Label title = new Label("Career Pilot AI");
        title.getStyleClass().add("nav-title");

        Button dashboardButton = navButton("Dashboard");
        dashboardButton.setOnAction(event -> showDashboard());

        Button profileButton = navButton("Resume Profile");
        profileButton.setOnAction(event -> showProfile());

        Button generateButton = navButton("Generate");
        generateButton.setOnAction(event -> showGenerate());

        Button documentsButton = navButton("Saved Documents");
        documentsButton.setOnAction(event -> showDocuments());

        nav.getChildren().addAll(title, dashboardButton, profileButton, generateButton, documentsButton);
        return nav;
    }

    private Button navButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private void showDashboard() {
        VBox content = page("Dashboard");
        Label summary = new Label("Career Pilot AI Lite helps create resume and cover letter drafts from a saved resume profile and job description.");
        summary.setWrapText(true);

        Button refreshButton = new Button("Load Saved Document Count");
        Label countLabel = new Label("Saved documents: unknown");
        refreshButton.setOnAction(event -> runBackground(
                () -> apiClient.getDocuments().size(),
                count -> countLabel.setText("Saved documents: " + count)
        ));

        content.getChildren().addAll(summary, refreshButton, countLabel);
        root.setCenter(content);
    }

    private void showProfile() {
        VBox content = page("Resume Profile");

        TextField fullNameField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextArea educationArea = bigTextArea();
        TextArea skillsArea = bigTextArea();
        TextArea experienceArea = bigTextArea();

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("Full name"), 0, 0);
        form.add(fullNameField, 1, 0);
        form.add(new Label("Email"), 0, 1);
        form.add(emailField, 1, 1);
        form.add(new Label("Phone"), 0, 2);
        form.add(phoneField, 1, 2);
        form.add(new Label("Education"), 0, 3);
        form.add(educationArea, 1, 3);
        form.add(new Label("Skills"), 0, 4);
        form.add(skillsArea, 1, 4);
        form.add(new Label("Experience"), 0, 5);
        form.add(experienceArea, 1, 5);
        GridPane.setHgrow(fullNameField, Priority.ALWAYS);
        GridPane.setHgrow(emailField, Priority.ALWAYS);
        GridPane.setHgrow(phoneField, Priority.ALWAYS);
        GridPane.setHgrow(educationArea, Priority.ALWAYS);
        GridPane.setHgrow(skillsArea, Priority.ALWAYS);
        GridPane.setHgrow(experienceArea, Priority.ALWAYS);

        Button saveButton = new Button("Save Profile");
        saveButton.setOnAction(event -> {
            if (isBlank(fullNameField.getText()) || isBlank(educationArea.getText()) || isBlank(skillsArea.getText())) {
                setStatus("Full name, education, and skills are required.");
                return;
            }
            ResumeProfile profile = new ResumeProfile();
            profile.setFullName(fullNameField.getText());
            profile.setEmail(emailField.getText());
            profile.setPhone(phoneField.getText());
            profile.setEducation(educationArea.getText());
            profile.setSkills(skillsArea.getText());
            profile.setExperience(experienceArea.getText());
            runBackground(() -> apiClient.saveResumeProfile(profile), saved -> setStatus("Profile saved."));
        });

        content.getChildren().addAll(form, saveButton);
        root.setCenter(content);

        runBackground(apiClient::getResumeProfile, profile -> {
            fullNameField.setText(value(profile.getFullName()));
            emailField.setText(value(profile.getEmail()));
            phoneField.setText(value(profile.getPhone()));
            educationArea.setText(value(profile.getEducation()));
            skillsArea.setText(value(profile.getSkills()));
            experienceArea.setText(value(profile.getExperience()));
            setStatus("Profile loaded.");
        });
    }

    private void showGenerate() {
        VBox content = page("Generate Documents");

        TextField companyField = new TextField();
        TextField jobTitleField = new TextField();
        TextArea jobDescriptionArea = bigTextArea();
        outputArea = bigTextArea();
        outputArea.setPromptText("Generated text will appear here. You can edit it before saving.");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("Company"), 0, 0);
        form.add(companyField, 1, 0);
        form.add(new Label("Job title"), 0, 1);
        form.add(jobTitleField, 1, 1);
        form.add(new Label("Job description"), 0, 2);
        form.add(jobDescriptionArea, 1, 2);
        GridPane.setHgrow(companyField, Priority.ALWAYS);
        GridPane.setHgrow(jobTitleField, Priority.ALWAYS);
        GridPane.setHgrow(jobDescriptionArea, Priority.ALWAYS);

        Button resumeButton = new Button("Generate Resume");
        resumeButton.setOnAction(event -> generateDocument("resume", companyField, jobTitleField, jobDescriptionArea));

        Button coverLetterButton = new Button("Generate Cover Letter");
        coverLetterButton.setOnAction(event -> generateDocument("cover letter", companyField, jobTitleField, jobDescriptionArea));

        Button saveButton = new Button("Save Edited Output");
        saveButton.setOnAction(event -> saveGeneratedOutput());

        HBox buttons = new HBox(10, resumeButton, coverLetterButton, saveButton);

        content.getChildren().addAll(form, buttons, new Label("Editable output"), outputArea);
        root.setCenter(content);
    }

    private void generateDocument(String type, TextField companyField, TextField jobTitleField, TextArea jobDescriptionArea) {
        if (isBlank(jobDescriptionArea.getText())) {
            setStatus("Job description is required.");
            return;
        }

        GenerateRequest request = new GenerateRequest();
        request.setCompany(companyField.getText());
        request.setJobTitle(jobTitleField.getText());
        request.setJobDescription(jobDescriptionArea.getText());
        lastGenerateRequest = request;
        currentDocumentType = type;
        outputArea.setText("");
        setStatus("Generating " + type + "...");

        if ("resume".equals(type)) {
            runBackground(() -> apiClient.generateResume(request), this::showGeneratedText);
        } else {
            runBackground(() -> apiClient.generateCoverLetter(request), this::showGeneratedText);
        }
    }

    private void showGeneratedText(GenerationResponse response) {
        currentDocumentType = response.getDocumentType();
        outputArea.setText(value(response.getContent()));
        setStatus("Generated " + currentDocumentType + ". Edit it before saving.");
    }

    private void saveGeneratedOutput() {
        if (lastGenerateRequest == null || isBlank(currentDocumentType)) {
            setStatus("Generate a document before saving.");
            return;
        }
        if (isBlank(outputArea.getText())) {
            setStatus("Generated content is required.");
            return;
        }

        GeneratedDocument document = new GeneratedDocument();
        document.setDocumentType(currentDocumentType);
        document.setCompany(lastGenerateRequest.getCompany());
        document.setJobTitle(lastGenerateRequest.getJobTitle());
        document.setJobDescription(lastGenerateRequest.getJobDescription());
        document.setContent(outputArea.getText());

        runBackground(() -> apiClient.saveDocument(document), saved -> setStatus("Document saved."));
    }

    private void showDocuments() {
        VBox content = page("Saved Documents");
        ListView<GeneratedDocument> documentList = new ListView<>();
        TextArea documentText = bigTextArea();
        documentText.setEditable(false);
        VBox.setVgrow(documentList, Priority.ALWAYS);
        VBox.setVgrow(documentText, Priority.ALWAYS);

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> loadDocuments(documentList));

        documentList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            if (selected != null && selected.getId() != null) {
                runBackground(() -> apiClient.getDocument(selected.getId()), document -> {
                    documentText.setText(value(document.getContent()));
                    setStatus("Document loaded.");
                });
            }
        });

        content.getChildren().addAll(refreshButton, documentList, new Label("Content"), documentText);
        root.setCenter(content);
        loadDocuments(documentList);
    }

    private void loadDocuments(ListView<GeneratedDocument> documentList) {
        runBackground(apiClient::getDocuments, documents -> {
            documentList.getItems().setAll(documents);
            setStatus("Loaded " + documents.size() + " saved documents.");
        });
    }

    private VBox page(String titleText) {
        VBox content = new VBox(12);
        content.getStyleClass().add("page");
        Label title = new Label(titleText);
        title.getStyleClass().add("page-title");
        content.getChildren().add(title);
        return content;
    }

    private TextArea bigTextArea() {
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setPrefRowCount(5);
        return textArea;
    }

    private <T> void runBackground(Work<T> work, Consumer<T> onSuccess) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return work.run();
            }
        };
        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> {
            Throwable error = task.getException();
            setStatus(error == null ? "Request failed." : error.getMessage());
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void setStatus(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String value(String value) {
        return value == null ? "" : value;
    }

    private interface Work<T> {
        T run() throws Exception;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
