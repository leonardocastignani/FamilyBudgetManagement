package it.unicam.cs.mpgc.jbudget125667.controller;

import it.unicam.cs.mpgc.jbudget125667.model.*;
import it.unicam.cs.mpgc.jbudget125667.service.*;

import javafx.application.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;

public class AddBankAccountController extends BaseController implements FamilyAware {
    @FXML private TextField accountNameField;
    @FXML private Label messageLabel;
    @FXML private Button addButton, backButton;

    private Family family;
    private final BankAccountService bankAccountService = new BankAccountService();

    @Override
    public void setFamily(Family family) { this.family = family; }

    @FXML
    public void initialize() {
        Platform.runLater(() -> this.accountNameField.getParent().requestFocus());
        this.clearOnFirstInteraction(this.messageLabel, this.accountNameField);
        this.addButton.setDefaultButton(true);
        this.backButton.setCancelButton(true);
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        String name = this.accountNameField.getText().trim();
        if (name.isEmpty()) {
            this.showErrorMessage(this.messageLabel, "Enter bank account name");
            return;
        }
        this.bankAccountService.createBankAccount(name, this.family);
        this.closeWindow(event);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        this.closeWindow(event);
    }
}