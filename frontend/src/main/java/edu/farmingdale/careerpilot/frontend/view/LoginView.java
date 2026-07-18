package edu.farmingdale.careerpilot.frontend.view;

import edu.farmingdale.careerpilot.frontend.service.AuthenticationService;
import edu.farmingdale.careerpilot.frontend.service.LoginValidator;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LoginView extends BorderPane {

    public LoginView(AuthenticationService authenticationService, Consumer<String> onAuthenticated) {
        LoginValidator loginValidator = new LoginValidator();
        getStyleClass().add("login-screen");

        VBox showcase = new VBox(18);
        showcase.getStyleClass().add("login-showcase");
        Label brand = new Label("Career Pilot AI");
        brand.getStyleClass().add("login-brand");
        Label headline = new Label("Build sharper career documents from one focused workspace.");
        headline.getStyleClass().add("login-showcase-title");
        headline.setWrapText(true);
        Label subhead = new Label("Profile memory, job brief generation, and saved drafts are organized like a study deck for your next application.");
        subhead.getStyleClass().add("login-showcase-copy");
        subhead.setWrapText(true);

        HBox signalRow = new HBox(10, signalCard("Profile", "Source"), signalCard("AI", "Drafts"), signalCard("Vault", "Saved"));
        signalRow.getStyleClass().add("login-signal-row");
        showcase.getChildren().addAll(brand, headline, subhead, signalRow);

        VBox card = new VBox(14);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(390);

        Label heading = new Label("Sign in");
        heading.getStyleClass().add("login-heading");

        Label description = new Label("Use demo credentials to enter the workspace.");
        description.getStyleClass().add("muted-label");

        TextField emailField = new TextField();
        emailField.setPromptText("name@example.com");
        emailField.getStyleClass().add("form-control");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("At least 6 characters");
        passwordField.getStyleClass().add("form-control");

        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText(passwordField.getPromptText());
        visiblePasswordField.getStyleClass().add("form-control");
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        CheckBox showPassword = new CheckBox("Show password");
        passwordField.visibleProperty().bind(showPassword.selectedProperty().not());
        passwordField.managedProperty().bind(passwordField.visibleProperty());
        visiblePasswordField.visibleProperty().bind(showPassword.selectedProperty());
        visiblePasswordField.managedProperty().bind(visiblePasswordField.visibleProperty());

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setWrapText(true);
        errorLabel.visibleProperty().bind(Bindings.isNotEmpty(errorLabel.textProperty()));
        errorLabel.managedProperty().bind(errorLabel.visibleProperty());

        Button loginButton = new Button("Enter Workspace");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(event -> {
            String email = emailField.getText() == null ? "" : emailField.getText().trim();
            String password = passwordField.getText();
            String validationMessage = loginValidator.validate(email, password);

            if (validationMessage != null) {
                errorLabel.setText(validationMessage);
                return;
            }
            if (!authenticationService.authenticate(email, password)) {
                errorLabel.setText("Unable to sign in with those credentials.");
                return;
            }

            errorLabel.setText("");
            onAuthenticated.accept(email);
        });

        Label demoNotice = new Label("Demo mode accepts any valid email and a password with at least 6 characters.");
        demoNotice.getStyleClass().add("demo-notice");
        demoNotice.setWrapText(true);

        card.getChildren().addAll(
                heading,
                description,
                fieldLabel("Email"),
                emailField,
                fieldLabel("Password"),
                passwordField,
                visiblePasswordField,
                showPassword,
                errorLabel,
                loginButton,
                demoNotice
        );

        HBox layout = new HBox(26, showcase, card);
        layout.getStyleClass().add("login-layout");
        layout.setAlignment(Pos.CENTER);
        HBox.setHgrow(showcase, Priority.ALWAYS);
        setCenter(layout);
    }

    private VBox signalCard(String value, String label) {
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("login-signal-value");
        Label labelText = new Label(label);
        labelText.getStyleClass().add("login-signal-label");
        VBox card = new VBox(4, valueLabel, labelText);
        card.getStyleClass().add("login-signal-card");
        return card;
    }

    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
        return label;
    }
}
