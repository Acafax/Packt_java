package org.example.springprojektzespolowy.dto.userGroupDto;

import org.example.springprojektzespolowy.dto.groupDto.GroupDto;
import org.example.springprojektzespolowy.dto.userDto.UserDto;

public record UserGroupDto(UserDto user, GroupDto group, String role) {
}
