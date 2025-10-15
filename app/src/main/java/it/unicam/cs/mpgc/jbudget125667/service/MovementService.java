package it.unicam.cs.mpgc.jbudget125667.service;

import it.unicam.cs.mpgc.jbudget125667.dao.*;
import it.unicam.cs.mpgc.jbudget125667.model.*;

import java.util.*;

public class MovementService {
    private final MovementDAO dao = new MovementDAO();

    public void add(Movement movement) {
        this.dao.save(movement);
    }

    public List<Movement> getUpcomingScheduledMovements(Family family) {
        return getScheduledMovements(new ArrayList<BankAccount>(family.getAccounts()));
    }

    public List<Movement> getUpcomingScheduledMovements(BankAccount account) {
        return getScheduledMovements(Collections.singletonList(account));
    }

    private List<Movement> getScheduledMovements(List<BankAccount> accounts) {
        return this.dao.findUpcomingScheduled(accounts);
    }
}