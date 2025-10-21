package org.example.springprojektzespolowy.dto.groupDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record  GroupDto (Long id, String groupName, String description, String currency, BigDecimal maxBudget, LocalDateTime startDate, LocalDateTime endDate, Long groupPhotoId){}
