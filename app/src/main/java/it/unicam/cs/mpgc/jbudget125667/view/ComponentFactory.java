package it.unicam.cs.mpgc.jbudget125667.view;

import it.unicam.cs.mpgc.jbudget125667.model.*;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.stream.*;

public final class ComponentFactory {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private ComponentFactory() {}

    public static Node createMovementRow(Movement movement, boolean showAccountName) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("movement-row");

        if (showAccountName) {
            row.getChildren().add(ControlFactory.createLabel(movement.getAccount().getAccountName(), "movement-account", 140));
        }

        Label dateLabel = ControlFactory.createLabel(movement.getDate().format(DATE_FORMATTER), "movement-date", 130);
        Label descriptionLabel = ControlFactory.createLabel(movement.getDescription(), "movement-desc", 400);
        Label categoryLabel = ControlFactory.createLabel(movement.getCategory().getName(), "movement-category", 120);

        double amount = movement.getAmount();
        String formattedAmount;
        String amountStyleClass;

        if (amount >= 0) {
            formattedAmount = String.format("+%.2f €", amount);
            amountStyleClass = "movement-amount-positive";
        } else {
            formattedAmount = String.format("%.2f €", amount);
            amountStyleClass = "movement-amount-negative";
        }

        Label amountLabel = ControlFactory.createLabel(formattedAmount, null, 120);
        amountLabel.getStyleClass().add(amountStyleClass);

        Label schedLabel = ControlFactory.createLabel(movement.isScheduled() ? "\u2714" : "\u2716", "movement-scheduled", 110);

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(dateLabel, descriptionLabel, categoryLabel, amountLabel, schedLabel, spacer);
        return row;
    }

    public static HBox createStatLine(String title, String value) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");
        HBox line = new HBox(8, titleLabel, valueLabel);
        line.setAlignment(Pos.CENTER_LEFT);
        return line;
    }

    public static PieChart createPieChart(String title, Map<String, Double> data) {
        PieChart chart = new PieChart();
        chart.setTitle(title);
        data.forEach((name, value) -> chart.getData().add(new PieChart.Data(name, value)));
        chart.setLegendVisible(true);
        chart.setLabelsVisible(true);
        chart.setPrefSize(500, 300);
        chart.getStyleClass().add("pie-chart");
        return chart;
    }

    public static HBox createAllMovementsHeader(boolean showAccountColumn) {
        HBox header = new HBox(12);
        header.getStyleClass().add("movements-header");
        if (showAccountColumn) header.getChildren().add(ControlFactory.createHeaderLabel("Bank Account", 140));
        header.getChildren().addAll(
            ControlFactory.createHeaderLabel("Date", 130), ControlFactory.createHeaderLabel("Description", 400),
            ControlFactory.createHeaderLabel("Category", 120), ControlFactory.createHeaderLabel("Amount", 120),
            ControlFactory.createHeaderLabel("Scheduled", 110)
        );
        return header;
    }

    public static ScrollPane createAllMovementsContent(List<Movement> movements, boolean showAccountColumn, Node header) {
        VBox contentBox = new VBox(8, header);
        movements.stream()
                 .sorted((m1, m2) -> m2.getDate().compareTo(m1.getDate()))
                 .map(m -> createMovementRow(m, showAccountColumn))
                 .forEach(contentBox.getChildren()::add);
        ScrollPane scroll = new ScrollPane(contentBox);
        scroll.setFitToWidth(true);
        return scroll;
    }

    public static HBox createDialogCloseButton(Stage stage) {
        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("close-dialog-btn");
        closeBtn.setOnAction(e -> stage.close());
        closeBtn.setCancelButton(true);
        HBox buttonBox = new HBox(closeBtn);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        return buttonBox;
    }

    public static HBox createDialogButtons(Stage stage, Runnable onSave) {
        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("save-btn");
        saveButton.setOnAction(e -> onSave.run());
        saveButton.setDefaultButton(true);

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("cancel-btn");
        cancelButton.setOnAction(e -> stage.close());
        cancelButton.setCancelButton(true);

        HBox buttonBox = new HBox(20, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        return buttonBox;
    }

    public static TableView<Movement> createSchedulerTableView(boolean isBankAccount) {
        TableView<Movement> table = new TableView<Movement>();
        table.getStyleClass().add("scheduler-table");

        List<TableColumn<Movement, ?>> columns = new ArrayList<TableColumn<Movement, ?>>();

        if (!isBankAccount) {
            // --- Colonna Bank Account ---
            TableColumn<Movement, String> accountCol = new TableColumn<Movement, String>("Bank Account");
            accountCol.getStyleClass().add("account-column");
            accountCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAccount().getAccountName()));
            accountCol.setPrefWidth(180);
            columns.add(accountCol);
        }        

        // --- Colonna Data ---
        TableColumn<Movement, LocalDate> dateCol = new TableColumn<Movement, LocalDate>("Date");
        dateCol.getStyleClass().add("date-column");
        dateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<LocalDate>(cellData.getValue().getDate()));
        dateCol.setPrefWidth(120);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        dateCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(formatter.format(date));
                }
            }
        });
        columns.add(dateCol);

        // --- Colonna Descrizione ---
        TableColumn<Movement, String> descriptionCol = new TableColumn<Movement, String>("Description");
        descriptionCol.getStyleClass().add("description-column");
        descriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        descriptionCol.setPrefWidth(250);
        columns.add(descriptionCol);

        // --- Colonna Categoria ---
        TableColumn<Movement, String> categoryCol = new TableColumn<Movement, String>("Category");
        categoryCol.getStyleClass().add("category-column");
        categoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory().getName()));
        categoryCol.setPrefWidth(180);
        columns.add(categoryCol);

        // --- Colonna Importo ---
        TableColumn<Movement, Double> amountCol = new TableColumn<Movement, Double>("Amount");
        amountCol.getStyleClass().add("amount-column");
        amountCol.setCellValueFactory(cellData -> new SimpleObjectProperty<Double>(cellData.getValue().getAmount()));
        amountCol.setCellFactory(tc -> new TableCell<Movement, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("amount-positive", "amount-negative");

                if (empty || item == null) {
                    setText(null);
                } else {
                    if (item < 0) {
                        setText(String.format("%.2f €", item));
                        getStyleClass().add("amount-negative");
                    } else {
                        setText(String.format("+%.2f €", item));
                        getStyleClass().add("amount-positive");
                    }
                }
            }
        });
        amountCol.setPrefWidth(150);
        columns.add(amountCol);

        table.getColumns().setAll(columns);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        return table;
    }

    public static VBox createLastMovementsBox(List<BankAccount> accounts) {
        VBox box = new VBox(10);
        Label title = ControlFactory.createSectionTitle("Last Movements");
        box.getChildren().add(title);

        List<Movement> lastMovements = accounts.stream()
                .flatMap(acc -> acc.getMovements().stream())
                .sorted(Comparator.comparing(Movement::getDate).reversed())
                .limit(3)
                .collect(Collectors.toList());

        if (lastMovements.isEmpty()) {
            box.getChildren().add(new Label("No movements recorded yet."));
        } else {
            lastMovements.forEach(mov -> box.getChildren().add(createMovementRow(mov, true)));
        }
        return box;
    }

    public static VBox createStatisticsBox(Map<String, String> stats) {
        VBox box = new VBox(10);
        Label title = ControlFactory.createSectionTitle("Statistics");
        box.getChildren().add(title);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(8);
        int row = 0;
        for (Map.Entry<String, String> entry : stats.entrySet()) {
            grid.add(ControlFactory.createLabel(entry.getKey(), "stat-key", 300), 0, row);
            grid.add(ControlFactory.createLabel(entry.getValue(), "stat-value", 200), 1, row);
            row++;
        }
        box.getChildren().add(grid);
        return box;
    }

    public static VBox createChartBox(String title, PieChart chart) {
        VBox box = new VBox(10);
        Label titleLabel = ControlFactory.createSectionTitle(title);
        box.getChildren().add(titleLabel);

        if (chart.getData().isEmpty()) {
            box.getChildren().add(new Label("No data to display for '" + title + "' chart."));
        } else {
            box.getChildren().add(chart);
        }
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("chart-box");
        return box;
    }

    public static VBox createBalanceSummaryCard(double accountingBalance, double availableBalance) {
        VBox card = new VBox();
        card.getStyleClass().add("dashboard-card");

        HBox contentBox = new HBox();
        contentBox.setAlignment(Pos.CENTER);

        // Contenitore per il Saldo Contabile (a sinistra)
        VBox accountingBox = new VBox(5);
        accountingBox.setAlignment(Pos.CENTER_LEFT);
        Label accountingLabel = ControlFactory.createLabel("Accounting Balance:", "balance-label", Region.USE_COMPUTED_SIZE);
        Label accountingValue = ControlFactory.createLabel(String.format("%.2f €", accountingBalance), "balance-value-small", Region.USE_COMPUTED_SIZE);
        accountingBox.getChildren().addAll(accountingLabel, accountingValue);

        // Contenitore per il Saldo Disponibile (a destra)
        VBox availableBox = new VBox(5);
        availableBox.setAlignment(Pos.CENTER_LEFT);
        Label availableLabel = ControlFactory.createLabel("Available Balance:", "balance-label", Region.USE_COMPUTED_SIZE);
        Label availableValue = ControlFactory.createLabel(String.format("%.2f €", availableBalance), "balance-value-small", Region.USE_COMPUTED_SIZE);
        availableBox.getChildren().addAll(availableLabel, availableValue);

        // Usa un Pane come spaziatore per spingere i due blocchi ai lati
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        contentBox.getChildren().addAll(accountingBox, spacer, availableBox);
        card.getChildren().add(contentBox);

        return card;
    }

    public static VBox createLastMovementsCard(List<BankAccount> accounts) {
        VBox card = new VBox(10);
        card.getStyleClass().add("dashboard-card");
        Label title = ControlFactory.createSectionTitle("Last Movements");
        card.getChildren().add(title);

        List<Movement> lastMovements = accounts.stream()
                .flatMap(acc -> acc.getMovements().stream())
                .sorted(Comparator.comparing(Movement::getDate).reversed())
                .limit(3)
                .collect(Collectors.toList());

        if (lastMovements.isEmpty()) {
            card.getChildren().add(new Label("No movements recorded yet."));
        } else {
            lastMovements.forEach(mov -> card.getChildren().add(createMovementRow(mov, accounts.size() > 1)));
        }
        return card;
    }

    public static VBox createChartCard(String title, Map<String, Double> data) {
        VBox card = new VBox(10);
        card.getStyleClass().add("dashboard-card");

        Map<String, Double> filteredData = data.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (filteredData.isEmpty()) {
            Label noDataLabel = new Label("No spending data to display for '" + title + "'.");
            card.getChildren().add(noDataLabel);
            card.setAlignment(Pos.CENTER);
            card.setMinHeight(200); // Assicura un'altezza minima anche senza grafico
        } else {
            PieChart chart = ChartFactory.createPieChart(title, ChartFactory.mapToPieChartData(filteredData));
            card.getChildren().add(chart);
            VBox.setVgrow(chart, Priority.ALWAYS);
            card.setAlignment(Pos.TOP_CENTER);
        }
        return card;
    }

    public static VBox createStatisticsCard(Map<String, String> stats) {
        VBox card = new VBox(10);
        card.getStyleClass().add("dashboard-card");
        Label title = ControlFactory.createSectionTitle("Statistics");
        card.getChildren().add(title);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(8);
        int row = 0;
        for (Map.Entry<String, String> entry : stats.entrySet()) {
            grid.add(ControlFactory.createLabel(entry.getKey(), "stat-key", 300), 0, row);
            grid.add(ControlFactory.createLabel(entry.getValue(), "stat-value", 200), 1, row);
            row++;
        }
        card.getChildren().add(grid);
        return card;
    }
}