package it.unicam.cs.mpgc.jbudget125667.controller;

import it.unicam.cs.mpgc.jbudget125667.model.*;
import it.unicam.cs.mpgc.jbudget125667.service.*;

import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.control.*;

import java.util.*;

public class FamilyLoginController extends BaseController {
    @FXML private TextField usernameField, passwordTextField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private Button loginButton, registerButton;

    private final FamilyService familyService = new FamilyService();
    private final DataTransferService dataTransferService = new DataTransferService();

    @FXML
    public void initialize() {
        Platform.runLater(() -> this.usernameField.getParent().requestFocus());
        this.clearOnFirstInteraction(this.messageLabel, this.usernameField, this.passwordField, this.passwordTextField);
        this.loginButton.setDefaultButton(true);
    }

    @FXML
    private void handleLogin() {
        String username = this.usernameField.getText().trim();
        String password = this.passwordField.isVisible() ? this.passwordField.getText().trim() : this.passwordTextField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            this.showErrorMessage(this.messageLabel, "Please complete all fields");
            return;
        }

        Optional<Family> familyOpt = this.familyService.authenticate(username, password);
        if (familyOpt.isPresent()) {
            this.navigateTo("/it/unicam/cs/mpgc/jbudget125667/fxml/FamilyHome.fxml", familyOpt.get());
        } else {
            this.showErrorMessage(this.messageLabel, "Incorrect credentials");
            Platform.runLater(() -> {
                this.usernameField.clear();
                this.passwordField.clear();
                this.passwordTextField.clear();
            });
        }
    }

    @FXML
    private void handleGoToRegister() {
        this.navigateTo("/it/unicam/cs/mpgc/jbudget125667/fxml/FamilyRegister.fxml");
    }

    @FXML
    private void toggleShowPassword() {
        this.togglePasswordVisibility(this.passwordField, this.passwordTextField);
    }

    @FXML
    private void handleImport() {
        try {
            Family importedFamily = this.dataTransferService.importFamilyData();
            if (importedFamily != null) {
                this.messageLabel.setText("Import successful for family: " + importedFamily.getUsername());
                this.messageLabel.getStyleClass().setAll("message-label", "success");
            }
        } catch (Exception e) {
            this.messageLabel.setText("Import failed: " + e.getMessage());
            this.messageLabel.getStyleClass().setAll("message-label", "error");
        }
    }
}