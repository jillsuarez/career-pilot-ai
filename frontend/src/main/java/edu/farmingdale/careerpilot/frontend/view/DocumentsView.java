package edu.farmingdale.careerpilot.frontend.view;

import edu.farmingdale.careerpilot.frontend.ApiClient;
import edu.farmingdale.careerpilot.frontend.model.GeneratedDocument;
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
        TextArea documentText = createTextArea();
        documentText.setEditable(false);
        VBox.setVgrow(documentList, Priority.ALWAYS);
        VBox.setVgrow(documentText, Priority.ALWAYS);

        Button refreshButton = new Button("Refresh");
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
