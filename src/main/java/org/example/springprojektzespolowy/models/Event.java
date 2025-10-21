package org.example.springprojektzespolowy.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesEvent;
import org.example.springprojektzespolowy.models.intermediateTable.UserEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Event {

    public Event(String name, String category, String description, String localization,LocalDateTime startEvent, LocalDateTime endEvent, String creator, List<UserEvent> users, Group group) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.localization = localization;
        this.startEvent = startEvent;
        this.endEvent = endEvent;
        this.creator = creator;
        this.users = users;
        this.group = group;
    }

    public Event(String name, String category, String description,String localization, LocalDateTime startEvent, LocalDateTime endEvent, String creator, Group group) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.localization = localization;
        this.startEvent = startEvent;
        this.endEvent = endEvent;
        this.creator = creator;
        this.group = group;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    private String description;

    private String localization;

    @Column(name = "start_event")
    private LocalDateTime startEvent;

    @Column(name = "end_event")
    private LocalDateTime endEvent;

    private String creator;

    @OneToMany(mappedBy = "event")
    private List<UserEvent> users;

    @OneToMany(mappedBy = "event")
    private List<ExpensesEvent> expenses;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

}
