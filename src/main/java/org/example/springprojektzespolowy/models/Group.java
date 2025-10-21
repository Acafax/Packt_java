package org.example.springprojektzespolowy.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesDocument;
import org.example.springprojektzespolowy.models.intermediateTable.UserGroup;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "groups")
public class Group {

    public Group(String name, String description, String currency, BigDecimal maxBudget ,LocalDateTime startDate, LocalDateTime endDate) {
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.maxBudget = maxBudget;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Group(String name, String description, String currency, BigDecimal maxBudget, LocalDateTime startDate, LocalDateTime endDate, Set<Document> documents, Set<Event> events) {
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.maxBudget = maxBudget;
        this.startDate = startDate;
        this.endDate = endDate;
        this.documents = documents;
        this.events = events;
    }

    public Group(Long id, String name, String description,String currency ,LocalDateTime startDate,LocalDateTime endDate, BigDecimal maxBudget,Set<Document> tickets, Set<Event> events) {
        this.id = id;
        this.endDate = endDate;
        this.startDate = startDate;
        this.currency = currency;
        this.description = description;
        this.name = name;
        this.documents = tickets;
        this.events = events;
        this.maxBudget= maxBudget;
    }

    public Group(Long id, String name, String description, String currency, BigDecimal maxBudget, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.maxBudget = maxBudget;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(name = "currency")
    private String currency;

    @Column(name = "maxBudget")
    private BigDecimal maxBudget;

    @Column(name = "profilePhotoId")
    private Long profilePhotoId;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", referencedColumnName = "id"
    , unique = true)
    private ProfilePhotoGroup photoGroup;


    @JsonManagedReference
    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
    Set<UserGroup> users;

    @JsonManagedReference
    @OneToMany(mappedBy = "group")
    Set<Document> documents;    

    @JsonManagedReference
    @OneToMany(mappedBy = "group")
    Set<Event> events;

    @OneToMany(mappedBy = "group")
    Set<Photo> photos;

}