package org.example.springprojektzespolowy.repositories.expenseRepos;

import org.example.springprojektzespolowy.models.intermediateTable.ExpensesEvent;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesEventKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpensesEventRepository extends JpaRepository<ExpensesEvent, ExpensesEventKey> {
}
