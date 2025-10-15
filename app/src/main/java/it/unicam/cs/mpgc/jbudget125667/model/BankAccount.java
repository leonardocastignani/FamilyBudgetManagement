package it.unicam.cs.mpgc.jbudget125667.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "bank_account")
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private long id;

    @Column(name = "account_name",
            nullable = false)
    private String accountName;

    @ManyToOne
    @JoinColumn(name = "family_id")
    @JsonBackReference
    private Family family;

    @OneToMany(mappedBy = "account",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Movement> movements = new HashSet<Movement>();

    public BankAccount() {}

    // Getter and Setter
    public void setId(long id) { this.id = id; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public void setFamily(Family family) { this.family = family; }
    public void setMovements(Set<Movement> movements) { this.movements = movements; }

    public long getId() { return this.id; }
    public String getAccountName() { return this.accountName; }
    public Family getFamily() { return this.family; }
    public Set<Movement> getMovements() { return this.movements; }
}