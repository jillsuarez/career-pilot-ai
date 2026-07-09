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
import javafx.scene.layout.VBox;

public class LoginView extends BorderPane {

    public LoginView(AuthenticationService authenticationService, Consumer<String> onAuthenticated) {
        LoginValidator loginValidator = new LoginValidator();
        getStyleClass().add("login-screen");

        VBox card = new VBox(12);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(400);

        Label brand = new Label("Career Pilot AI");
        brand.getStyleClass().add("login-brand");

        Label heading = new Label("Welcome back");
        heading.getStyleClass().add("login-heading");

        Label description = new Label("Sign in to manage your job search.");
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

        Button loginButton = new Button("Sign in");
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

        Label demoNotice = new Label("Demo mode: use any valid email and a password with at least 6 characters.");
        demoNotice.getStyleClass().add("demo-notice");
        demoNotice.setWrapText(true);

        card.getChildren().addAll(
                brand,
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

        setCenter(card);
        BorderPane.setAlignment(card, Pos.CENTER);
    }

    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
        return label;
    }

}
