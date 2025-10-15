package it.unicam.cs.mpgc.jbudget125667.view;

import it.unicam.cs.mpgc.jbudget125667.MainApp;
import it.unicam.cs.mpgc.jbudget125667.controller.*;
import it.unicam.cs.mpgc.jbudget125667.model.*;
import it.unicam.cs.mpgc.jbudget125667.service.*;

import javafx.application.*;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.*;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class DialogManager {

    private record AddMovementForm(
            TextField descriptionField, DatePicker datePicker, ComboBox<Category> categoryComboBox,
            TextField amountField, CheckBox scheduledCheckBox, Label messageLabel
    ) {}

    private DialogManager() {}

    public static void showAllMovementsDialog(List<BankAccount> accounts) {
        Stage stage = ViewUtils.createBaseDialogStage("All Movements");
        boolean showAccountColumn = accounts.size() > 1;

        List<Movement> allMovements = accounts.stream()
                .flatMap(acc -> acc.getMovements().stream())
                .collect(Collectors.toList());

        Node header = ComponentFactory.createAllMovementsHeader(showAccountColumn);
        Node content = ComponentFactory.createAllMovementsContent(allMovements, showAccountColumn, header);
        Node closeButton = ComponentFactory.createDialogCloseButton(stage);

        VBox root = new VBox(12, content, closeButton);
        root.getStyleClass().add("movements-dialog");
        root.setPadding(new Insets(12));
        ViewUtils.finalizeAndShowDialog(stage, root);
    }

    public static void showAddMovementDialog(BankAccount account, Consumer<BankAccount> onSuccess) {
        Stage stage = ViewUtils.createBaseDialogStage("Add New Movement");
        AddMovementForm form = createAddMovementForm();
        Runnable saveAction = () -> handleSaveMovement(account, form, () -> onSuccess.accept(account), stage);
        HBox buttons = ComponentFactory.createDialogButtons(stage, saveAction);

        VBox root = new VBox(10,
                ControlFactory.createDialogTitle("ADD NEW MOVEMENT"),
                new Label("Description:"), form.descriptionField,
                new Label("Date:"), form.datePicker,
                new Label("Category:"), form.categoryComboBox,
                new Label("Amount:"), form.amountField,
                form.scheduledCheckBox,
                form.messageLabel,
                buttons
        );
        styleAddMovementDialog(root);
        root.setPrefWidth(500);
        Platform.runLater(root::requestFocus);
        ViewUtils.finalizeAndShowDialog(stage, root);
    }

    private static void handleSaveMovement(BankAccount account, AddMovementForm form, Runnable onSuccess, Stage stage) {
        try {
            String description = form.descriptionField.getText().trim();
            LocalDate date = form.datePicker.getValue();
            Category category = form.categoryComboBox.getValue();
            String amountText = form.amountField.getText().replace(',', '.');

            if (description.isEmpty() || date == null || category == null || amountText.isEmpty()) {
                form.messageLabel.setText("Please complete all fields.");
                return;
            }
            Movement movement = new Movement();
            movement.setDescription(description);
            movement.setDate(date);
            movement.setCategory(category);
            movement.setAmount(Double.parseDouble(amountText));
            movement.setScheduled(form.scheduledCheckBox.isSelected());
            movement.setAccount(account);

            new MovementService().add(movement);
            onSuccess.run();
            stage.close();
        } catch (NumberFormatException ex) {
            form.messageLabel.setText("Invalid amount format.");
        } catch (Exception ex) {
            form.messageLabel.setText("An unexpected error occurred.");
            ex.printStackTrace();
        }
    }

    private static AddMovementForm createAddMovementForm() {
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("e.g., Supermarket shopping");
        DatePicker datePicker = new DatePicker();

        final Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                // Disabilita la cella se la data è precedente a oggi.
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    getStyleClass().add("disabled-date"); // Aggiunge una classe per lo stile CSS.
                }
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);

        ComboBox<Category> categoryComboBox = ControlFactory.createCategoryComboBox();
        TextField amountField = ControlFactory.createNumericTextField("e.g., -50.00");
        CheckBox scheduledCheckBox = new CheckBox("Scheduled Movement");
        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("message-label");
        return new AddMovementForm(descriptionField, datePicker, categoryComboBox, amountField, scheduledCheckBox, messageLabel);
    }

    private static void styleAddMovementDialog(VBox root) {
        root.getStyleClass().add("add-movement-dialog");
        root.getChildren().forEach(node -> {
            if (node instanceof TextField || node instanceof DatePicker || node instanceof ComboBox) {
                node.getStyleClass().add("add-movement-field");
            } else if (node instanceof CheckBox) {
                node.getStyleClass().add("add-movement-checkbox");
            } else if (node instanceof Label && ((Label) node).getText().endsWith(":")) {
                node.getStyleClass().add("add-movement-label");
            }
        });
    }

    public static void showSchedulerDialog(Object obj) {
        MovementService movementService = new MovementService();
        List<Movement> scheduledMovements;
        String dialogTitle;
        boolean isBankAccount;

        // Controlla il tipo dell'oggetto di contesto e recupera i dati di conseguenza
        if (obj instanceof Family) {
            Family family = (Family) obj;
            scheduledMovements = movementService.getUpcomingScheduledMovements(family);
            dialogTitle = "Upcoming Scheduled Payments";
            isBankAccount = false;
        } else if (obj instanceof BankAccount) {
            BankAccount account = (BankAccount) obj;
            scheduledMovements = movementService.getUpcomingScheduledMovements(account);
            dialogTitle = String.format("Scheduled Movements for %s", account.getAccountName());
            isBankAccount = true;
        } else {
            // Caso di errore: tipo di oggetto non supportato
            System.err.println("Type Error in showSchedulerDialog(): " + obj.getClass().getName());
            return;
        }

        Stage stage = ViewUtils.createBaseDialogStage(dialogTitle);

        TableView<Movement> schedulerTable = ComponentFactory.createSchedulerTableView(isBankAccount);
        schedulerTable.setItems(FXCollections.observableArrayList(scheduledMovements));

        VBox root = new VBox(15,
                ControlFactory.createDialogTitle("Scheduler"),
                schedulerTable,
                ComponentFactory.createDialogCloseButton(stage)
        );
        root.setPadding(new Insets(15));
        root.setPrefWidth(!isBankAccount ? 900 : 720);

        ViewUtils.finalizeAndShowDialog(stage, root);
    }

    public static void showAddAccountDialog(Family family, Runnable onDialogClose) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/it/unicam/cs/mpgc/jbudget125667/fxml/AddBankAccount.fxml"));
            Parent root = loader.load();

            AddBankAccountController controller = loader.getController();
            controller.setFamily(family);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Bank Account");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(MainApp.getPrimaryStage());
            dialogStage.setResizable(false);
            dialogStage.setMinWidth(750);
            dialogStage.setMinHeight(500);
            dialogStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/it/unicam/cs/mpgc/jbudget125667/icon/icon.png")));

            Scene scene = new Scene(root);
            scene.getStylesheets().add(MainApp.class.getResource("/it/unicam/cs/mpgc/jbudget125667/style/style.css").toExternalForm());
            dialogStage.setScene(scene);

            dialogStage.showAndWait();

            if (onDialogClose != null) {
                onDialogClose.run();
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Qui si potrebbe mostrare un alert di errore
        }
    }
}