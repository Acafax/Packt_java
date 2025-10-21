package org.example.springprojektzespolowy.dto.expenses;

import org.example.springprojektzespolowy.dto.userEvent.UserWithRoleDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ExpenseWithoutDocumentsAndEventsDto(Long id, String name , String description,String category , BigDecimal price, LocalDateTime dateOfExpense, LocalDateTime dateOfAdding, String creator,
                                                  List<UserWithRoleDto> participants) {
}
