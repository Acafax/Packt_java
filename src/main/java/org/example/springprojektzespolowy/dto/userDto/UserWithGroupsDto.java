package org.example.springprojektzespolowy.dto.userDto;

import org.example.springprojektzespolowy.dto.groupDto.GroupRoleDto;
import org.example.springprojektzespolowy.dto.userGroupDto.UserGroupDto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public record UserWithGroupsDto(Long id, String UId , String userName, String email, String country,
                                LocalDate dateOfBirth,Long photoId, List<GroupRoleDto> groupList){
}
