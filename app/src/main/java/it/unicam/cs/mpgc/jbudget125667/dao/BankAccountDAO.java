package it.unicam.cs.mpgc.jbudget125667.dao;

import it.unicam.cs.mpgc.jbudget125667.model.*;
import it.unicam.cs.mpgc.jbudget125667.util.*;

import java.util.*;
import org.hibernate.*;

public class BankAccountDAO extends AbstractDAO<BankAccount> {
    public BankAccountDAO() {
        super(BankAccount.class);
    }

    public List<BankAccount> findByFamilyId(Long familyId) {
        return execute(session ->
            session.createQuery("FROM BankAccount b WHERE b.family.id = :fid", BankAccount.class)
                   .setParameter("fid", familyId)
                   .getResultList()
        );
    }

    public BankAccount findByIdWithMovements(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "SELECT ba FROM BankAccount ba LEFT JOIN FETCH ba.movements WHERE ba.id = :id", BankAccount.class)
                .setParameter("id", id)
                .uniqueResult();
        }
    }
}