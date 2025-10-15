package it.unicam.cs.mpgc.jbudget125667.controller;

import it.unicam.cs.mpgc.jbudget125667.model.*;
import it.unicam.cs.mpgc.jbudget125667.service.*;
import it.unicam.cs.mpgc.jbudget125667.view.*;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import java.util.*;

public class BankAccountHomeController extends BaseController implements FamilyAware {
    @FXML private Text accountNameText;
    @FXML private VBox dashboardContainer;
    @FXML private Button backButton;

    private BankAccount account;
    private Family family;
    private final BankAccountService bankAccountService = new BankAccountService();
    private final ReportingService reportingService = new ReportingService();

    @FXML
    public void initialize() {
        this.backButton.setCancelButton(true);
    }

    @Override
    public void setFamily(Family family) {
        this.family = family;
    }

    public void setBankAccount(BankAccount account, Family family) {
        this.account = account;
        this.setFamily(family);
        this.refreshView();
    }

    private void refreshView() {
        // Ricarica i dati per essere sicuro di avere le ultime modifiche
        this.account = bankAccountService.getAccountByIdWithMovements(this.account.getId());

        this.accountNameText.setText(this.capitalize(this.account.getAccountName()));

        this.dashboardContainer.getChildren().clear();

        // Card Saldi
        double accountingBalance = bankAccountService.getBalanceNotScheduled(this.account);
        double availableBalance = bankAccountService.getBalance(this.account);
        this.dashboardContainer.getChildren().add(ComponentFactory.createBalanceSummaryCard(accountingBalance, availableBalance));

        // Card Ultimi movimenti
        this.dashboardContainer.getChildren().add(ComponentFactory.createLastMovementsCard(List.of(this.account)));

        // Card Statistiche
        Map<String, String> accountStats = reportingService.getAccountStatistics(this.account);
        this.dashboardContainer.getChildren().add(ComponentFactory.createStatisticsCard(accountStats));

        // Card Grafico Spese per Categoria
        Map<String, Double> spendingByCategory = reportingService.getSpendingByCategory(List.of(this.account));
        this.dashboardContainer.getChildren().add(ComponentFactory.createChartCard("Spending by Category", spendingByCategory));

        // Card Grafico Entrate vs Uscite
        Map<String, Double> incomeVsExpense = reportingService.getIncomeVsExpense(List.of(this.account));
        this.dashboardContainer.getChildren().add(ComponentFactory.createChartCard("Income vs Expense", incomeVsExpense));
    }

    @FXML
    private void handleAddMovement() {
        DialogManager.showAddMovementDialog(this.account, acc -> refreshView());
    }

    @FXML
    private void handleShowScheduler() {
        DialogManager.showSchedulerDialog(this.account);
    }

    @FXML
    private void handleShowAllMovements() {
        DialogManager.showAllMovementsDialog(List.of(this.account));
    }

    @FXML
    private void handleBackToFamily() {
        ViewUtils.navigateTo("/it/unicam/cs/mpgc/jbudget125667/fxml/FamilyHome.fxml", this.family);
    }
}