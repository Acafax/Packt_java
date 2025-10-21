package org.example.springprojektzespolowy.dto.userGroupDto;

import org.example.springprojektzespolowy.dto.userDto.UserDto;

public record UserGroupWithoutGroupsDto(UserDto user, String role) {
}
