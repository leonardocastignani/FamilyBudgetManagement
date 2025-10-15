package it.unicam.cs.mpgc.jbudget125667.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "family")
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "family_id")
    private long id;

    @Column(name = "username",
            nullable = false,
            unique = true)
    private String username;

    @Column(name = "password",
            nullable = false)
    private String password;

    @OneToMany(mappedBy = "family",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<BankAccount> accounts = new HashSet<BankAccount>();

    public Family() {}

    // Getter and Setter
    public void setId(long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setAccounts(Set<BankAccount> accounts) { this.accounts = accounts; }

    public long getId() { return this.id; }    
    public String getUsername() { return this.username; }    
    public String getPassword() { return this.password; }    
    public Set<BankAccount> getAccounts() { return this.accounts; }
}