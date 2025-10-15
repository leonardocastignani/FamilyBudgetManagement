package it.unicam.cs.mpgc.jbudget125667.dao;

import it.unicam.cs.mpgc.jbudget125667.model.*;
import it.unicam.cs.mpgc.jbudget125667.util.*;

import org.hibernate.*;
import java.time.*;
import java.util.*;

public class MovementDAO extends AbstractDAO<Movement> {
    public MovementDAO() { super(Movement.class); }

    public List<Movement> findByAccountId(Long accountId) {
        return execute(session ->
            session.createQuery("FROM Movement m WHERE m.account.id = :aid", Movement.class)
                   .setParameter("aid", accountId)
                   .getResultList()
        );
    }

    public double sumByCategoryAndMonth(List<BankAccount> accounts, Category category, int year, int month) {
        if (accounts == null || accounts.isEmpty()) {
            return 0.0;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1);

            Double result = session.createQuery(
                "SELECT SUM(m.amount) FROM Movement m WHERE m.account IN (:accounts) AND m.category = :category AND m.date >= :startDate AND m.date < :endDate AND m.amount < 0", Double.class)
                .setParameter("accounts", accounts)
                .setParameter("category", category)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();

            return (result == null) ? 0.0 : Math.abs(result);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public List<Movement> findUpcomingScheduled(List<BankAccount> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            return Collections.emptyList();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Movement WHERE account IN (:accounts) AND isScheduled = true AND date >= :today ORDER BY date ASC", Movement.class)
                .setParameter("accounts", accounts)
                .setParameter("today", LocalDate.now())
                .list();
        }
    }

    public List<Movement> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Movement", Movement.class).list();
        }
    }

    public List<Movement> findDueScheduledMovements(LocalDate date) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Movement WHERE isScheduled = true AND date <= :currentDate", Movement.class)
                    .setParameter("currentDate", date)
                    .getResultList();
        }
    }
}