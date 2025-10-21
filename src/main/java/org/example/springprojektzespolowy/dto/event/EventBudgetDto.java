package org.example.springprojektzespolowy.dto.event;

import org.example.springprojektzespolowy.dto.userEvent.UserWithRoleDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record EventBudgetDto (Long id, String eventName, String category, String description, String localization, LocalDateTime startEvent, LocalDateTime endEvent, Set<UserWithRoleDto> members){
}
