package org.example.springprojektzespolowy.models.intermediateTable;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ExpensesUserKey implements Serializable {

    private Long userId;

    private Long expenseId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExpensesUserKey that = (ExpensesUserKey) o;
        return Objects.equals(userId, that.userId) && Objects.equals(expenseId, that.expenseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, expenseId);
    }
}
