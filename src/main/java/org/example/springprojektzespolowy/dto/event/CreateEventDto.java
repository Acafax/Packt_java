package org.example.springprojektzespolowy.dto.event;

import org.example.springprojektzespolowy.models.Group;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CreateEventDto(String eventName ,String category, String description,String localization ,LocalDateTime startEvent, LocalDateTime endEvent, String creator, List<String> participants)  {
}
