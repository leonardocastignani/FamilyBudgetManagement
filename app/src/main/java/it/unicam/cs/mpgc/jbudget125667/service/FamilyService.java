package it.unicam.cs.mpgc.jbudget125667.service;

import it.unicam.cs.mpgc.jbudget125667.dao.*;
import it.unicam.cs.mpgc.jbudget125667.model.*;

import java.math.*;
import java.security.*;
import java.util.*;

public class FamilyService {
    private final FamilyDAO dao = new FamilyDAO();
    private final BankAccountService bankAccountService = new BankAccountService();

    public void register(String username, String password) {
        Family family = new Family();
        family.setUsername(username);
        family.setPassword(this.hashPassword(password));
        this.dao.save(family);
    }

    public Optional<Family> authenticate(String username, String password) {
        String hashedPassword = this.hashPassword(password);
        return this.dao.findByUsernameAndPassword(username, hashedPassword);
    }

    public Optional<Family> getFamilyByUsername(String username) {
        return this.dao.findByUsername(username);
    }

    public Family getFamilyById(long id) {
        return this.dao.findById(id);
    }

    public Family getByIdWithDetails(Long id) {
        return this.dao.findByIdWithDetails(id);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashtext = new StringBuilder(no.toString(16));
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }
            return hashtext.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public double getAccountingBalance(Family family) {
        if (family == null || family.getAccounts() == null) return 0.0;
        return family.getAccounts().stream()
                .mapToDouble(this.bankAccountService::getBalanceNotScheduled)
                .sum();
    }

    public double getAvailableBalance(Family family) {
        if (family == null || family.getAccounts() == null) return 0.0;
        return family.getAccounts().stream()
                .mapToDouble(this.bankAccountService::getBalance)
                .sum();
    }

    /**
     * Salva una nuova famiglia e, a cascata, tutti i suoi conti e movimenti.
     * Usato principalmente per la funzionalità di import.
     * @param family L'oggetto famiglia da salvare.
     */
    public void saveFamilyWithDetails(Family family) {
        // Assicura che le relazioni siano bidirezionali prima di salvare
        family.getAccounts().forEach(account -> {
            account.setFamily(family);
            account.getMovements().forEach(movement -> movement.setAccount(account));
        });
        dao.save(family);
    }
}