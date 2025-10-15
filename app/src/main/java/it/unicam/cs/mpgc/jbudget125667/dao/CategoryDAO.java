package it.unicam.cs.mpgc.jbudget125667.dao;

import it.unicam.cs.mpgc.jbudget125667.model.*;
import it.unicam.cs.mpgc.jbudget125667.util.*;

import java.util.*;
import org.hibernate.*;

public class CategoryDAO extends AbstractDAO<Category> {
    public CategoryDAO() {
        super(Category.class);
    }

    public List<Category> findAllWithChildren() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.children", Category.class).list();
        }
    }
}