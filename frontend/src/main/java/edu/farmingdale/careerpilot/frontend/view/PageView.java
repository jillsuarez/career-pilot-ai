package edu.farmingdale.careerpilot.frontend.view;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public abstract class PageView extends VBox {

    protected PageView(String titleText) {
        super(14);
        getStyleClass().add("page");

        Label title = new Label(titleText);
        title.getStyleClass().add("page-title");
        getChildren().add(title);
    }

    protected TextArea createTextArea() {
        TextArea textArea = new TextArea();
        textArea.getStyleClass().add("form-control");
        textArea.setWrapText(true);
        textArea.setPrefRowCount(5);
        return textArea;
    }

    protected Label fieldLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
        return label;
    }

    protected boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    protected String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
