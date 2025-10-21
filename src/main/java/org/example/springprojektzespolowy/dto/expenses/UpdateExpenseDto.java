package org.example.springprojektzespolowy.dto.expenses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record UpdateExpenseDto(Long id,String name , String description, String category , BigDecimal price, LocalDateTime dateOfExpense,
                               String creator,List<String> participants) {
}
