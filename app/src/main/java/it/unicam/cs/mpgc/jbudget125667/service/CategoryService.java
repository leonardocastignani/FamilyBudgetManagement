package it.unicam.cs.mpgc.jbudget125667.service;

import it.unicam.cs.mpgc.jbudget125667.dao.*;
import it.unicam.cs.mpgc.jbudget125667.model.*;

import java.util.*;

public class CategoryService {
    private final CategoryDAO dao = new CategoryDAO();
    
    public List<Category> getAllCategories() {
        return this.dao.findAll();
    }

    public List<Category> getAllCategoriesWithChildren() {
        return this.dao.findAllWithChildren();
    }

    public Category getCategoryById(long id) {
        return this.dao.findById(id);
    }
}