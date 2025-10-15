package it.unicam.cs.mpgc.jbudget125667.controller;

import it.unicam.cs.mpgc.jbudget125667.model.*;
import it.unicam.cs.mpgc.jbudget125667.service.*;
import it.unicam.cs.mpgc.jbudget125667.view.*;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import java.util.*;

public class FamilyHomeController extends BaseController implements FamilyAware {
    @FXML private Text familyNameText;
    @FXML private VBox dashboardContainer, reportsContainer, accountsContainer;
    @FXML private Button backButton;

    private final ReportingService reportingService = new ReportingService();
    private final FamilyService familyService = new FamilyService();
    private final DataTransferService dataTransferService = new DataTransferService();
    private Family currentFamily;

    @FXML
    public void initialize() {
        this.backButton.setCancelButton(true);
    }

    @Override
    public void setFamily(Family family) {
        this.currentFamily = family;
        if (family != null) {
            this.familyNameText.setText(this.capitalize(this.currentFamily.getUsername()));
            this.refreshDashboard();
        }
    }

    private void refreshDashboard() {
        this.dashboardContainer.getChildren().clear();
        this.reportsContainer.getChildren().clear();
        this.accountsContainer.getChildren().clear();

        this.currentFamily = familyService.getFamilyById(this.currentFamily.getId());
        List<BankAccount> accounts = new ArrayList<BankAccount>(this.currentFamily.getAccounts());

        this.populateDashboardTab(accounts);
        this.populateReportsTab(accounts);
        this.populateAccountsTab(accounts);
    }

    private void populateDashboardTab(List<BankAccount> accounts) {
        // Saldo Contabile e Disponibile
        double accountingBalance = familyService.getAccountingBalance(this.currentFamily);
        double availableBalance = familyService.getAvailableBalance(this.currentFamily);
        VBox balanceSummaryCard = ComponentFactory.createBalanceSummaryCard(accountingBalance, availableBalance);

        // Ultimi movimenti
        VBox lastMovementsCard = ComponentFactory.createLastMovementsCard(accounts);

        // Grafico Spese per Categoria
        Map<String, Double> spendingByCategory = reportingService.getSpendingByCategory(accounts);
        VBox categoryChartCard = ComponentFactory.createChartCard("Spending by Category", spendingByCategory);

        // Aggiunge le card direttamente al contenitore VBox per un layout verticale
        this.dashboardContainer.getChildren().addAll(balanceSummaryCard, lastMovementsCard, categoryChartCard);
    }

    private void populateReportsTab(List<BankAccount> accounts) {
        // Pulisce il contenitore prima di aggiungere i nuovi elementi
        reportsContainer.getChildren().clear();

        // 1. Card Statistiche Generali
        Map<String, String> familyStats = this.reportingService.getFamilyStatistics(this.currentFamily);
        VBox statisticsBox = ComponentFactory.createStatisticsCard(familyStats);

        // 2. Grafico Spese per Conto
        Map<String, Double> spendingByAccount = this.reportingService.getSpendingByAccount(this.currentFamily);
        VBox accountChartCard = ComponentFactory.createChartCard("Spending by Account", spendingByAccount);

        // 3. Grafico Spese per Categoria
        Map<String, Double> spendingByCategory = reportingService.getSpendingByCategory(accounts);
        VBox categoryChartCard = ComponentFactory.createChartCard("Spending by Category", spendingByCategory);

        // 4. Grafico Entrate vs Uscite
        Map<String, Double> incomeVsExpense = reportingService.getIncomeVsExpense(accounts);
        VBox incomeChartCard = ComponentFactory.createChartCard("Income vs Expenses", incomeVsExpense);

        // Aggiunge tutte le card verticalmente al contenitore
        reportsContainer.getChildren().addAll(statisticsBox, accountChartCard, categoryChartCard, incomeChartCard);
    }

    private void populateAccountsTab(List<BankAccount> accounts) {
        Label accountsTitle = ControlFactory.createSectionTitle("Your Bank Accounts");
        this.accountsContainer.getChildren().add(accountsTitle);

        if (accounts.isEmpty()) {
            this.accountsContainer.getChildren().add(new Label("No bank accounts found. Add one to get started!"));
        } else {
            accounts.sort(Comparator.comparing(BankAccount::getAccountName));
            for (BankAccount account : accounts) {
                Button accountButton = new Button(account.getAccountName());
                accountButton.getStyleClass().add("account-list-btn");
                accountButton.setMaxWidth(Double.MAX_VALUE);
                accountButton.setOnAction(e -> navigateToBankAccount(account));
                accountsContainer.getChildren().add(accountButton);
            }
        }
    }

    private void navigateToBankAccount(BankAccount account) {
        ViewUtils.navigateTo("/it/unicam/cs/mpgc/jbudget125667/fxml/BankAccountHome.fxml", account);
    }

    @FXML
    private void handleAddAccount() {
        DialogManager.showAddAccountDialog(this.currentFamily, this::refreshDashboard);
    }

    @FXML
    private void handleShowScheduler() {
        DialogManager.showSchedulerDialog(this.currentFamily);
    }

    @FXML
    private void handleShowAllMovements() {
        DialogManager.showAllMovementsDialog(new ArrayList<BankAccount>(this.currentFamily.getAccounts()));
    }

    @FXML
    private void handleExport() {
        // Assicurati che la famiglia sia completamente caricata dal DB
        Family familyToExport = this.familyService.getFamilyById(this.currentFamily.getId());
        this.dataTransferService.exportFamilyData(familyToExport);
    }

    @FXML
    private void handleLogout() {
        ViewUtils.navigateTo("/it/unicam/cs/mpgc/jbudget125667/fxml/FamilyLogin.fxml", null);
    }
}