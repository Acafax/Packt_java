package org.example.springprojektzespolowy.dto.groupDto;

import java.time.LocalDateTime;

public record UpdateGroupDto(Long id, String groupName, String description, String currency, LocalDateTime startDate, LocalDateTime endDate) {
}
