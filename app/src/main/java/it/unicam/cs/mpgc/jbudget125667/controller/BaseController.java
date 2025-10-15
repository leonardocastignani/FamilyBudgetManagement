package it.unicam.cs.mpgc.jbudget125667.controller;

import it.unicam.cs.mpgc.jbudget125667.MainApp;

import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.*;

public abstract class BaseController {

    protected void navigateTo(String fxmlPath, Object data) {
        try {
            MainApp.setRoot(fxmlPath, data);
        } catch (IOException e) {
            handleNavigationError(e);
        }
    }

    protected void navigateTo(String fxmlPath) {
        navigateTo(fxmlPath, null);
    }

    protected void handleNavigationError(IOException e) {
        System.err.println("Navigation Error: " + e.getMessage());
        e.printStackTrace();
    }

    protected void showErrorMessage(Label messageLabel, String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
        }
    }

    protected void clearOnFirstInteraction(Label messageLabel, TextField... fields) {
        for (TextField field : fields) {
            field.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.isEmpty()) showErrorMessage(messageLabel, "");
            });
        }
    }

    protected void togglePasswordVisibility(PasswordField passwordField, TextField passwordTextField) {
        boolean isPasswordVisible = passwordTextField.isVisible();
        passwordTextField.setManaged(!isPasswordVisible);
        passwordTextField.setVisible(!isPasswordVisible);
        passwordField.setManaged(isPasswordVisible);
        passwordField.setVisible(isPasswordVisible);

        if (!isPasswordVisible) {
            passwordTextField.setText(passwordField.getText());
        } else {
            passwordField.setText(passwordTextField.getText());
        }
        passwordTextField.requestFocus();
        passwordTextField.positionCaret(passwordTextField.getText().length());
    }

    protected String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    protected void closeWindow(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}