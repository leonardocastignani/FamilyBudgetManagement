package it.unicam.cs.mpgc.jbudget125667.service;

import it.unicam.cs.mpgc.jbudget125667.model.*;

import java.util.*;
import java.util.stream.*;

public class ReportingService {

    private final BankAccountService bankAccountService = new BankAccountService();

    private Stream<Movement> getMovementsStream(List<BankAccount> accounts, boolean includeScheduled) {
        return accounts.stream()
                .flatMap(acc -> acc.getMovements().stream())
                .filter(m -> includeScheduled || !m.isScheduled());
    }

    public Map<String, String> getFamilyStatistics(Family family) {
        List<BankAccount> accounts = new ArrayList<BankAccount>(family.getAccounts());
        List<Movement> movements = getMovementsStream(accounts, false).collect(Collectors.toList());
        DoubleSummaryStatistics balanceStats = accounts.stream()
                .mapToDouble(this.bankAccountService::getBalanceNotScheduled)
                .summaryStatistics();

        Map<String, String> stats = new LinkedHashMap<String, String>();
        stats.put("Number of Bank Accounts:", String.valueOf(accounts.size()));
        stats.put("Number of Movements:", String.valueOf(movements.size()));
        stats.put("Total Balance:", String.format("%.2f €", balanceStats.getSum()));
        stats.put("Max Balance:", String.format("%.2f €", balanceStats.getMax()));
        stats.put("Min Balance:", String.format("%.2f €", balanceStats.getMin()));
        stats.put("Avg Balance:", String.format("%.2f €", balanceStats.getAverage()));

        return stats;
    }

    public Map<String, String> getAccountStatistics(BankAccount account) {
        List<Movement> movements = this.getMovementsStream(List.of(account), false).collect(Collectors.toList());
        DoubleSummaryStatistics movementStats = movements.stream()
                .mapToDouble(Movement::getAmount)
                .summaryStatistics();

        Map<String, String> stats = new LinkedHashMap<String, String>();
        stats.put("Number of Movements:", String.valueOf(movements.size()));
        stats.put("Bank Account Balance:", String.format("%.2f €", this.bankAccountService.getBalanceNotScheduled(account)));
        stats.put("Max Amount Movement:", String.format("%.2f €", movementStats.getMax()));
        stats.put("Min Amount Movement:", String.format("%.2f €", movementStats.getMin()));
        stats.put("Avg Amount Movement:", String.format("%.2f €", movementStats.getAverage()));
        return stats;
    }

    public Map<String, Double> getSpendingByCategory(List<BankAccount> accounts) {
        return this.getMovementsStream(accounts, false)
                .filter(m -> m.getAmount() < 0)
                .collect(Collectors.groupingBy(
                        m -> m.getCategory().getName(),
                        Collectors.summingDouble(m -> Math.abs(m.getAmount()))
                ));
    }

    public Map<String, Double> getSpendingByAccount(Family family) {
        return family.getAccounts().stream()
                .collect(Collectors.toMap(
                        BankAccount::getAccountName,
                        acc -> getMovementsStream(List.of(acc), false)
                               .filter(m -> m.getAmount() < 0)
                               .mapToDouble(m -> Math.abs(m.getAmount()))
                               .sum()
                ));
    }

    public Map<String, Double> getIncomeVsExpense(List<BankAccount> accounts) {
        Map<String, Double> incomeVsExpense = this.getMovementsStream(accounts, false)
                .collect(Collectors.partitioningBy(
                        m -> m.getAmount() >= 0,
                        Collectors.summingDouble(Movement::getAmount)
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey() ? "Income" : "Expenses",
                        entry -> entry.getKey() ? entry.getValue() : Math.abs(entry.getValue())
                ));
        incomeVsExpense.putIfAbsent("Income", 0.0);
        incomeVsExpense.putIfAbsent("Expenses", 0.0);
        return incomeVsExpense;
    }
}