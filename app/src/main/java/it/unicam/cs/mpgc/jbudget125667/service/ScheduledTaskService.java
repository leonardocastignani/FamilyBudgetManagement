package it.unicam.cs.mpgc.jbudget125667.service;

import it.unicam.cs.mpgc.jbudget125667.dao.*;
import it.unicam.cs.mpgc.jbudget125667.model.*;
import it.unicam.cs.mpgc.jbudget125667.util.*;

import org.hibernate.*;
import java.time.*;
import java.util.*;

public class ScheduledTaskService {

    private final MovementDAO movementDAO = new MovementDAO();

    public void processDueMovements() {
        List<Movement> dueMovements = this.movementDAO.findDueScheduledMovements(LocalDate.now());
        if (dueMovements.isEmpty()) {
            System.out.println("No due scheduled movements to process.");
            return;
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            for (Movement movement : dueMovements) {
                movement.setScheduled(false);
                session.merge(movement);
            }
            transaction.commit();
            System.out.println("Successfully processed " + dueMovements.size() + " due scheduled movements.");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Failed to process scheduled movements.");
            e.printStackTrace();
        }
    }
}