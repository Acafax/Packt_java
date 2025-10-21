package org.example.springprojektzespolowy.dto.invitation;

import org.example.springprojektzespolowy.dto.groupDto.GroupDto;
import org.example.springprojektzespolowy.dto.userDto.UserDto;

public record InvitationDto(UserDto invitedUser, GroupDto group, UserDto inviter ) {
}
