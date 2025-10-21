package org.example.springprojektzespolowy.dto.userEvent;

import org.example.springprojektzespolowy.dto.userDto.UserDto;
import org.example.springprojektzespolowy.dto.userDto.UserDtoUidNameEmail;

public record UserWithRoleDto(UserDtoUidNameEmail user, String role) {
}
