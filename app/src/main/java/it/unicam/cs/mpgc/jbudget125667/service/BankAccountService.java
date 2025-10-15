package it.unicam.cs.mpgc.jbudget125667.service;

import it.unicam.cs.mpgc.jbudget125667.dao.*;
import it.unicam.cs.mpgc.jbudget125667.model.*;

import java.util.function.*;

public class BankAccountService {
    private final BankAccountDAO dao = new BankAccountDAO();

    public void createBankAccount(String accountName, Family family) {
        BankAccount account = new BankAccount();
        account.setAccountName(accountName);
        account.setFamily(family);
        this.dao.save(account);
    }

    public BankAccount getAccountById(long id) {
        return this.dao.findById(id);
    }

    public BankAccount getAccountByIdWithMovements(long id) {
        return this.dao.findByIdWithMovements(id);
    }

    public double getBalance(BankAccount account) {
        return this.calculateBalance(account, movement -> true);
    }

    public double getBalanceNotScheduled(BankAccount account) {
        return this.calculateBalance(account, m -> !m.isScheduled());
    }

    public double getScheduledBalance(BankAccount account) {
        return this.calculateBalance(account, Movement::isScheduled);
    }

    private double calculateBalance(BankAccount account, Predicate<Movement> filterPredicate) {
        if (account == null || account.getMovements() == null) {
            return 0.0;
        }
        return account.getMovements().stream()
                .filter(filterPredicate)
                .mapToDouble(Movement::getAmount)
                .sum();
    }
}