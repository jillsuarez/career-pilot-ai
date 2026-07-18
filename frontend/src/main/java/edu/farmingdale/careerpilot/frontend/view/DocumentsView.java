package edu.farmingdale.careerpilot.frontend.view;

import edu.farmingdale.careerpilot.frontend.ApiClient;
import edu.farmingdale.careerpilot.frontend.model.GeneratedDocument;
import edu.farmingdale.careerpilot.frontend.service.UiTaskRunner;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class DocumentsView extends PageView {

    public DocumentsView(ApiClient apiClient, UiTaskRunner taskRunner) {
        super("Saved Documents");

        ListView<GeneratedDocument> documentList = new ListView<>();
        Label emptyTitle = new Label("No saved documents yet");
        emptyTitle.getStyleClass().add("empty-state-heading");

        Label emptyMessage = new Label("Generate a resume or cover letter to see saved documents here.");
        emptyMessage.getStyleClass().add("muted-label");
        emptyMessage.setWrapText(true);

        VBox emptyState = new VBox(6, emptyTitle, emptyMessage);
        emptyState.getStyleClass().add("empty-state");
        documentList.setPlaceholder(emptyState);

        TextArea documentText = createTextArea();
        documentText.setEditable(false);
        documentText.setPromptText("Select a saved document to view its content.");

        Button refreshButton = new Button("Refresh Vault");
        refreshButton.getStyleClass().add("secondary-button");
        refreshButton.setTooltip(createTooltip("Refresh the list of saved documents."));

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.getStyleClass().add("small-progress");
        progressIndicator.setMaxSize(18, 18);
        progressIndicator.setVisible(false);
        progressIndicator.setManaged(false);

        Label vaultTitle = new Label("Draft Vault");
        vaultTitle.getStyleClass().add("panel-title");
        Label vaultSubtitle = new Label("Saved resumes and cover letters");
        vaultSubtitle.getStyleClass().add("muted-label");
        vaultSubtitle.setWrapText(true);
        HBox vaultActions = new HBox(10, refreshButton, progressIndicator);
        vaultActions.getStyleClass().add("quick-actions");
        VBox listHeader = new VBox(3, vaultTitle, vaultSubtitle, vaultActions);

        VBox vaultPanel = new VBox(14, listHeader, documentList);
        vaultPanel.getStyleClass().addAll("surface-panel", "vault-list-panel");
        VBox.setVgrow(documentList, Priority.ALWAYS);

        Label readerTitle = new Label("Document Reader");
        readerTitle.getStyleClass().add("panel-title");
        Label readerSubtitle = new Label("Review saved output without leaving the workspace.");
        readerSubtitle.getStyleClass().add("muted-label");
        readerSubtitle.setWrapText(true);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label readerPill = new Label("AI Draft");
        readerPill.getStyleClass().add("surface-pill");
        HBox readerHeader = new HBox(10, new VBox(3, readerTitle, readerSubtitle), spacer, readerPill);
        readerHeader.getStyleClass().add("output-header");

        VBox readerPanel = new VBox(12, readerHeader, documentText);
        readerPanel.getStyleClass().addAll("surface-panel", "vault-reader-panel");
        HBox.setHgrow(readerPanel, Priority.ALWAYS);
        VBox.setVgrow(documentText, Priority.ALWAYS);

        HBox vaultLayout = new HBox(16, vaultPanel, readerPanel);
        vaultLayout.getStyleClass().add("document-vault");
        VBox.setVgrow(vaultLayout, Priority.ALWAYS);

        refreshButton.setOnAction(event -> loadDocuments(
                apiClient,
                taskRunner,
                documentList,
                documentText,
                refreshButton,
                progressIndicator
        ));

        documentList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            if (selected == null) {
                documentText.clear();
                return;
            }
            if (selected.getId() == null) {
                documentText.setText(valueOrEmpty(selected.getContent()));
                return;
            }
            taskRunner.setStatus("Loading document...");
            taskRunner.run(() -> apiClient.getDocument(selected.getId()), document -> {
                documentText.setText(valueOrEmpty(document.getContent()));
                taskRunner.setStatus("Document loaded.");
            });
        });

        getChildren().add(vaultLayout);
        loadDocuments(apiClient, taskRunner, documentList, documentText, refreshButton, progressIndicator);
    }

    private void loadDocuments(
            ApiClient apiClient,
            UiTaskRunner taskRunner,
            ListView<GeneratedDocument> documentList,
            TextArea documentText,
            Button refreshButton,
            ProgressIndicator progressIndicator
    ) {
        refreshButton.setDisable(true);
        progressIndicator.setVisible(true);
        progressIndicator.setManaged(true);
        taskRunner.setStatus("Loading saved documents...");
        taskRunner.run(apiClient::getDocuments, documents -> {
            documentList.getItems().setAll(documents);
            if (documents.isEmpty()) {
                documentText.clear();
            } else {
                documentList.getSelectionModel().selectFirst();
            }
            refreshButton.setDisable(false);
            progressIndicator.setVisible(false);
            progressIndicator.setManaged(false);
            taskRunner.setStatus("Loaded " + documents.size() + " saved documents.");
        }, () -> {
            refreshButton.setDisable(false);
            progressIndicator.setVisible(false);
            progressIndicator.setManaged(false);
        });
    }

    private Tooltip createTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.millis(200));
        return tooltip;
    }
}
