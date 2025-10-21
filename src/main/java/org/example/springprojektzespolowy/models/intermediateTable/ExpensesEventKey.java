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
public class ExpensesEventKey implements Serializable {

    private Long eventId;

    private Long expenseId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExpensesEventKey that = (ExpensesEventKey) o;
        return Objects.equals(eventId, that.eventId) && Objects.equals(expenseId, that.expenseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, expenseId);
    }
}
