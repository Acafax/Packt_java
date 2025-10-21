package org.example.springprojektzespolowy.dto.expenses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CreateExpenseDto(String name , String description, String category , BigDecimal price, LocalDateTime dateOfExpense,
                               List<String> participants ) {}
