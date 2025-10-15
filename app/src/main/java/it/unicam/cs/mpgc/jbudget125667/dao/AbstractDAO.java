package it.unicam.cs.mpgc.jbudget125667.dao;

import it.unicam.cs.mpgc.jbudget125667.util.*;

import org.hibernate.*;

import java.util.*;
import java.util.function.*;

public abstract class AbstractDAO<T> {

    private final Class<T> entityClass;

    protected AbstractDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected <R> R execute(Function<Session, R> action) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return action.apply(session);
        }
    }

    protected void executeInsideTransaction(Function<Session, Void> action) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            action.apply(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Database transaction failed", e);
        }
    }

    public void save(T entity) {
        executeInsideTransaction(session -> {
            session.persist(entity);
            return null;
        });
    }

    public void delete(T entity) {
        executeInsideTransaction(session -> {
            session.remove(session.contains(entity) ? entity : session.merge(entity));
            return null;
        });
    }

    public T findById(Long id) {
        return execute(session -> session.find(this.entityClass, id));
    }

    public List<T> findAll() {
        return execute(session ->
            session.createQuery("FROM " + this.entityClass.getSimpleName(), this.entityClass).list()
        );
    }
}