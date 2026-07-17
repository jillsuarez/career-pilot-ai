package edu.farmingdale.careerpilot.frontend.view;

import edu.farmingdale.careerpilot.frontend.ApiClient;
import edu.farmingdale.careerpilot.frontend.model.GeneratedDocument;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import edu.farmingdale.careerpilot.frontend.service.UiTaskRunner;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DocumentsView extends PageView {

    public DocumentsView(ApiClient apiClient, UiTaskRunner taskRunner) {
        super("Saved Documents");

        ListView<GeneratedDocument> documentList = new ListView<>();
        Label emptyTitle = new Label("No saved documents yet");
        emptyTitle.getStyleClass().add("empty-state-heading");

        Label emptyMessage = new Label(
                "Generate a resume or cover letter to see your saved documents here."
        );
        emptyMessage.getStyleClass().add("muted-label");
        emptyMessage.setWrapText(true);

        VBox emptyState = new VBox(6, emptyTitle, emptyMessage);
        emptyState.getStyleClass().add("empty-state");

        documentList.setPlaceholder(emptyState);

        TextArea documentText = createTextArea();
        documentText.setEditable(false);
        VBox.setVgrow(documentList, Priority.ALWAYS);
        VBox.setVgrow(documentText, Priority.ALWAYS);

        Button refreshButton = new Button("Refresh");

        Tooltip refreshTooltip = new Tooltip("Refresh the list of saved documents.");
        refreshTooltip.setShowDelay(Duration.millis(200));
        refreshButton.setTooltip(refreshTooltip);

        refreshButton.setOnAction(event -> loadDocuments(apiClient, taskRunner, documentList));

        documentList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selected) -> {
            if (selected != null && selected.getId() != null) {
                taskRunner.run(() -> apiClient.getDocument(selected.getId()), document -> {
                    documentText.setText(valueOrEmpty(document.getContent()));
                    taskRunner.setStatus("Document loaded.");
                });
            }
        });

        getChildren().addAll(refreshButton, documentList, new Label("Content"), documentText);
        loadDocuments(apiClient, taskRunner, documentList);
    }

    private void loadDocuments(ApiClient apiClient, UiTaskRunner taskRunner, ListView<GeneratedDocument> documentList) {
        taskRunner.run(apiClient::getDocuments, documents -> {
            documentList.getItems().setAll(documents);
            taskRunner.setStatus("Loaded " + documents.size() + " saved documents.");
        });
    }
}
