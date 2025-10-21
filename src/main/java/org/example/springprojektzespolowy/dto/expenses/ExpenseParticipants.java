package org.example.springprojektzespolowy.dto.expenses;

import org.example.springprojektzespolowy.dto.userEvent.UserWithRoleDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record ExpenseParticipants(Long id, String name , String description,String category , BigDecimal price, LocalDateTime dateOfExpense, LocalDateTime dateOfAdding, String creator,List<UserWithRoleDto> participants) {
}
