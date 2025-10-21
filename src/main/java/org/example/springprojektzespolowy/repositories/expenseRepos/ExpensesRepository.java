package org.example.springprojektzespolowy.repositories.expenseRepos;

import org.example.springprojektzespolowy.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpensesRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e LEFT JOIN FETCH e.group eg LEFT JOIN FETCH e.events ee LEFT JOIN FETCH e.participants ep " +
            "LEFT JOIN FETCH e.documents ed WHERE e.group.id=:groupId")
    List<Expense> getExpenseByGroup_Id(@Param("groupId")Long  groupId);
}
