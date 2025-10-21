package org.example.springprojektzespolowy.dto.userEvent;
import org.example.springprojektzespolowy.dto.event.EventDto;
import java.util.Set;

public record UserEventDto(EventDto event, Set<UserWithRoleDto> members) {
}
