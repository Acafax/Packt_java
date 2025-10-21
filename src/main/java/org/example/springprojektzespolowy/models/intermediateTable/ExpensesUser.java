package org.example.springprojektzespolowy.models.intermediateTable;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springprojektzespolowy.models.Expense;
import org.example.springprojektzespolowy.models.User;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ExpensesUser {


    @EmbeddedId
    private ExpensesUserKey expensesUserKey;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name ="user_id")
    @MapsId("userId")
    private User user;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name ="expense_id")
    @MapsId("expenseId")
    private Expense expense;


    private String role;


}
