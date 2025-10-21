package org.example.springprojektzespolowy.dto.expenses;

import org.example.springprojektzespolowy.dto.documents.DocumentDto;
import org.example.springprojektzespolowy.dto.event.EventDto;
import org.example.springprojektzespolowy.dto.userDto.UserDto;
import org.example.springprojektzespolowy.dto.userEvent.UserWithRoleDto;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesDocument;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesEvent;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesUser;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record ExpenseDto(Long id, String name ,String description,String category , BigDecimal price, LocalDateTime dateOfExpense, LocalDateTime dateOfAdding,String creator,List<UserWithRoleDto> participants, Set<DocumentDto> documents, Set<EventDto> events) {
}
