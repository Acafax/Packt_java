package org.example.springprojektzespolowy.repositories.expenseRepos;

import org.example.springprojektzespolowy.models.intermediateTable.ExpensesUser;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesUserKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpensesUserRepository extends JpaRepository<ExpensesUser, ExpensesUserKey> {
    void deleteByExpense_Id(Long expenseId);

    void deleteByUser_UId(String userUId);

    void deleteByUser_UIdAndExpense_Id(String userUId, Long expenseId);
}
