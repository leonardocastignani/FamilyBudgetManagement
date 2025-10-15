package it.unicam.cs.mpgc.jbudget125667.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "movement")
public class Movement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movement_id")
    private long id;

    @Column(name = "amount",
            nullable = false)
    private double amount;

    @Column(name = "date",
            nullable = false)
    private LocalDate date;

    @Column(name = "description",
            nullable = false)
    private String description;

    @Column(name = "is_scheduled",
            nullable = false)
    private boolean isScheduled;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonBackReference
    private BankAccount account;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Movement() {}

    // Getter and Setter
    public void setId(long id) { this.id = id; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
    public void setScheduled(boolean scheduled) { this.isScheduled = scheduled; }
    public void setAccount(BankAccount account) { this.account = account; }
    public void setCategory(Category category) { this.category = category; }

    public long getId() { return this.id; }
    public double getAmount() { return this.amount; }
    public LocalDate getDate() { return this.date; }
    public String getDescription() { return this.description; }
    public boolean isScheduled() { return this.isScheduled; }
    public BankAccount getAccount() { return this.account; }
    public Category getCategory() { return this.category; }
}