package it.unicam.cs.mpgc.jbudget125667.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private long id;

    @Column(name = "category_name",
            nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    private Category parent;

    @OneToMany(mappedBy = "parent",
               fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Category> children = new HashSet<Category>();

    @OneToMany(mappedBy = "category",
               fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Movement> movements = new HashSet<Movement>();

    public Category() {}

    // Getter and Setter
    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setParent(Category parent) { this.parent = parent; }
    public void setChildren(Set<Category> children) { this.children = children; }
    public void setMovements(Set<Movement> movements) { this.movements = movements; }

    public long getId() { return this.id; }
    public String getName() { return this.name; }
    public Category getParent() { return this.parent; }
    public Set<Category> getChildren() { return this.children; }
    public Set<Movement> getMovements() { return this.movements; }
}