package org.example.springprojektzespolowy.dto.groupDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CreateGroupDto(String groupName, String description, String currency , LocalDateTime startDate, LocalDateTime endDate, BigDecimal maxBudget, List<String> guestEmails) {
}
