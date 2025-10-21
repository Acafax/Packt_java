package org.example.springprojektzespolowy.dto.groupDto;

import org.example.springprojektzespolowy.dto.userDto.UserDto;
import org.example.springprojektzespolowy.dto.userEvent.UserWithRoleDto;

import java.time.LocalDateTime;
import java.util.Set;

public record GroupWithUsersDto(Long id, String groupName, String description, String currency, LocalDateTime startDate, LocalDateTime endDate, Long photoGroupId,Set<UserWithRoleDto> users) {
}
