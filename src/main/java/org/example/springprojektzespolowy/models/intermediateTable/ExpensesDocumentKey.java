package org.example.springprojektzespolowy.models.intermediateTable;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class ExpensesDocumentKey implements Serializable {

    private Long documentId;

    private Long expenseId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExpensesDocumentKey that = (ExpensesDocumentKey) o;
        return Objects.equals(documentId, that.documentId) && Objects.equals(expenseId, that.expenseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId, expenseId);
    }
}
