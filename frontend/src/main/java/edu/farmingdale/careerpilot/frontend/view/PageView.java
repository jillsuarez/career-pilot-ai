package edu.farmingdale.careerpilot.frontend.view;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public abstract class PageView extends VBox {

    protected PageView(String titleText) {
        super(12);
        getStyleClass().add("page");

        Label title = new Label(titleText);
        title.getStyleClass().add("page-title");
        getChildren().add(title);
    }

    protected TextArea createTextArea() {
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setPrefRowCount(5);
        return textArea;
    }

    protected boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    protected String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
