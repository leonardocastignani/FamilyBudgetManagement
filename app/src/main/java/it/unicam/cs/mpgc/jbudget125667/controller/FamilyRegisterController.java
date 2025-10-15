package it.unicam.cs.mpgc.jbudget125667.controller;

import it.unicam.cs.mpgc.jbudget125667.service.*;

import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.control.*;

public class FamilyRegisterController extends BaseController {
    @FXML private TextField usernameField, passwordTextField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private Button registerButton, backButton;

    private final FamilyService familyService = new FamilyService();

    @FXML
    public void initialize() {
        Platform.runLater(() -> this.usernameField.getParent().requestFocus());
        this.clearOnFirstInteraction(this.messageLabel, this.usernameField, this.passwordField, this.passwordTextField);
        this.registerButton.setDefaultButton(true);
        this.backButton.setCancelButton(true);
    }

    @FXML
    private void handleRegister() {
        String username = this.usernameField.getText().trim();
        String password = this.passwordField.isVisible() ? this.passwordField.getText().trim() : this.passwordTextField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            this.showErrorMessage(this.messageLabel, "Please complete all fields");
            return;
        }
        try {
            this.familyService.register(username, password);
            this.navigateTo("/it/unicam/cs/mpgc/jbudget125667/fxml/FamilyLogin.fxml");
        } catch (Exception e) {
            // This could be a unique constraint violation on username
            this.showErrorMessage(this.messageLabel, "Username already exists or an error occurred.");
        }
    }

    @FXML
    private void handleBack() {
        this.navigateTo("/it/unicam/cs/mpgc/jbudget125667/fxml/FamilyLogin.fxml");
    }

    @FXML
    private void toggleShowPassword() {
        this.togglePasswordVisibility(this.passwordField, this.passwordTextField);
    }
}