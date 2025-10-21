package org.example.springprojektzespolowy.models.intermediateTable;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springprojektzespolowy.models.Event;
import org.example.springprojektzespolowy.models.Expense;
import org.example.springprojektzespolowy.models.User;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ExpensesEvent {

    @EmbeddedId
    private ExpensesEventKey expensesEventKey;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name ="event_id")
    @MapsId("eventId")
    private Event event;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name ="expense_id")
    @MapsId("expenseId")
    private Expense expense;

    private String role;

}
