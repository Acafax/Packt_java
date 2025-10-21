package org.example.springprojektzespolowy.dto.mappers;

import org.example.springprojektzespolowy.dto.groupDto.GroupDto;
import org.example.springprojektzespolowy.dto.groupDto.GroupRoleDto;
import org.example.springprojektzespolowy.dto.groupDto.GroupWithUsersDto;
import org.example.springprojektzespolowy.dto.userDto.UserDto;
import org.example.springprojektzespolowy.dto.userEvent.UserWithRoleDto;
import org.example.springprojektzespolowy.dto.userGroupDto.UserGroupDto;
import org.example.springprojektzespolowy.dto.userGroupDto.UserGroupWithoutGroupsDto;
import org.example.springprojektzespolowy.models.Group;
import org.example.springprojektzespolowy.models.User;
import org.example.springprojektzespolowy.models.intermediateTable.UserGroup;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class UserGroupMapper {

    public List<GroupRoleDto> convertMap (List<UserGroup> userGroups){
        return userGroups.stream().map(userGroup ->{
            return new GroupRoleDto(
                convert(userGroup.getGroup()),
                userGroup.getRole()
            );
        }).toList();
    }

    public UserGroupDto convert(UserGroup userGroup){
        return new UserGroupDto(
                convert(userGroup.getUser()),
                convert(userGroup.getGroup()),
                userGroup.getRole()
        );


    }

    public GroupWithUsersDto convertWithEmptyMembers(Group group){
        return new GroupWithUsersDto(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCurrency(),
                group.getStartDate(),
                group.getEndDate(),
                group.getProfilePhotoId(),
                Set.of()
        );
    }

    public GroupWithUsersDto convert(Group group, Set<UserWithRoleDto> members){
        return new GroupWithUsersDto(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCurrency(),
                group.getStartDate(),
                group.getEndDate(),
                group.getProfilePhotoId(),
                members
        );
    }

    public List<UserGroupWithoutGroupsDto> convertWithoutGroup (List<UserGroup> userGroups){
        return userGroups.stream().map(userGroup -> new UserGroupWithoutGroupsDto(
                convert(userGroup.getUser()),
                userGroup.getRole()
        )).toList();
    }

    private UserDto convert(User user) {
        return new UserDto(
                user.getId(),
                user.getUId(),
                user.getName(),
                user.getEmail(),
                user.getCountry(),
                user.getDateOfBirth(),
                user.getProfilePhotoId()
        );
    }

    private GroupDto convert(Group group) {
        return new GroupDto(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCurrency(),
                group.getMaxBudget(),
                group.getStartDate(),
                group.getEndDate(),
                group.getProfilePhotoId()
        );
    }
}
