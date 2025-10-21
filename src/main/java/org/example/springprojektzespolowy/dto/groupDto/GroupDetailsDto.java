package org.example.springprojektzespolowy.dto.groupDto;

import org.example.springprojektzespolowy.dto.event.EventDto;
import org.example.springprojektzespolowy.dto.documents.DocumentDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record GroupDetailsDto (Long groupId, String groupName, String description, String currency, LocalDateTime startDate, LocalDateTime endDate, BigDecimal maxBudget, Integer numbreOfMembers,
                               Long photoGroupId,
                               Set<DocumentDto> documents, Set<EventDto> events){
}
