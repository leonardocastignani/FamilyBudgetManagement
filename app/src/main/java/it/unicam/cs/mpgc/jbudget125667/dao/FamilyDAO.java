package it.unicam.cs.mpgc.jbudget125667.dao;

import it.unicam.cs.mpgc.jbudget125667.model.*;
import it.unicam.cs.mpgc.jbudget125667.util.*;

import java.util.*;
import org.hibernate.*;
import org.hibernate.query.*;

public class FamilyDAO extends AbstractDAO<Family> {
    public FamilyDAO() {
        super(Family.class);
    }

    @Override
    public Family findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "SELECT DISTINCT f FROM Family f " +
                "LEFT JOIN FETCH f.accounts a " +
                "LEFT JOIN FETCH a.movements " +
                "WHERE f.id = :id", Family.class)
                .setParameter("id", id)
                .uniqueResult();
        }
    }

    public Family findByUsernameAndPasswordWithDetails(String username, String password) {
        return this.execute(session -> {
            Query<Family> q = session.createQuery(
                "SELECT DISTINCT f FROM Family f LEFT JOIN FETCH f.accounts a LEFT JOIN FETCH a.movements WHERE f.username=:u AND f.password=:p", Family.class);
            q.setParameter("u", username);
            q.setParameter("p", password);
            return q.uniqueResult();
        });
    }

    public Family findByIdWithDetails(Long id) {
        return this.execute(session -> {
            Query<Family> q = session.createQuery(
                "SELECT DISTINCT f FROM Family f LEFT JOIN FETCH f.accounts a LEFT JOIN FETCH a.movements WHERE f.id = :id", Family.class);
            q.setParameter("id", id);
            return q.uniqueResult();
        });
    }

    public Optional<Family> findByUsernameAndPassword(String username, String hashedPassword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Family WHERE username = :username AND password = :password", Family.class)
                    .setParameter("username", username)
                    .setParameter("password", hashedPassword)
                    .uniqueResultOptional();
        }
    }

    public Optional<Family> findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Family WHERE username = :username", Family.class)
                    .setParameter("username", username)
                    .uniqueResultOptional();
        }
    }

    public Family findFamilyByIdForExport(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT DISTINCT f FROM Family f " +
                    "LEFT JOIN FETCH f.accounts a " +
                    "LEFT JOIN FETCH a.movements m " +
                    "LEFT JOIN FETCH m.category c " +
                    "LEFT JOIN FETCH c.parent " +
                    "WHERE f.id = :id", Family.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}