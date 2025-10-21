package org.example.springprojektzespolowy.models.intermediateTable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springprojektzespolowy.models.Document;
import org.example.springprojektzespolowy.models.Expense;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ExpensesDocument {

    @EmbeddedId
    private ExpensesDocumentKey expensesDocumentKey;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "expense_id")
    @MapsId("expenseId")
    private Expense expense;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "document_id")
    @MapsId("documentId")
    private Document document;

    private String role;


}
