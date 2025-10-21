package org.example.springprojektzespolowy.models;


import jakarta.persistence.*;
import lombok.*;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesDocument;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesEvent;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    public Expense(String name, String description, String category, BigDecimal price, LocalDateTime dateOfExpense, LocalDateTime dateOfAdding) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.dateOfExpense = dateOfExpense;
        this.dateOfAdding = dateOfAdding;
    }

    public Expense(String name, String description, String category , BigDecimal price, LocalDateTime dateOfExpense, LocalDateTime dateOfAdding, Group group) {
        this.name = name;
        this.description = description;
        this.category=category;
        this.price = price;
        this.dateOfExpense = dateOfExpense;
        this.dateOfAdding = dateOfAdding;
        this.group = group;
    }


    public Expense(String name, String description, String category , BigDecimal price, LocalDateTime dateOfExpense, LocalDateTime dateOfAdding, String creator, Group group) {
        this.name = name;
        this.description = description;
        this.category=category;
        this.price = price;
        this.dateOfExpense = dateOfExpense;
        this.dateOfAdding = dateOfAdding;
        this.creator = creator;
        this.group = group;
    }

    public Expense(String name, String description, String category,BigDecimal price, LocalDateTime dateOfExpense, LocalDateTime dateOfAdding, String creator) {
        this.name = name;
        this.description = description;
        this.category=category;
        this.price = price;
        this.dateOfExpense = dateOfExpense;
        this.dateOfAdding = dateOfAdding;
        this.creator=creator;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String category;

    private BigDecimal price;

    private LocalDateTime dateOfExpense;

    private LocalDateTime dateOfAdding;

    private String creator;

    @OneToMany(mappedBy = "expense")
    private List<ExpensesUser> participants;

    @OneToMany(mappedBy = "expense")
    private Set<ExpensesDocument> documents;

    @OneToMany(mappedBy = "expense")
    private Set<ExpensesEvent> events;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}
