package org.example.springprojektzespolowy.repositories.expenseRepos;

import org.example.springprojektzespolowy.models.intermediateTable.ExpensesDocument;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesDocumentKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpensesDocumentRepository extends JpaRepository<ExpensesDocument, ExpensesDocumentKey> {
}
