package org.example.springprojektzespolowy.dto.mappers;


import org.example.springprojektzespolowy.dto.groupDto.GroupDetailsDto;
import org.example.springprojektzespolowy.dto.userDto.*;
import org.example.springprojektzespolowy.dto.userEvent.UserWithRoleDto;
import org.example.springprojektzespolowy.models.User;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesUser;
import org.example.springprojektzespolowy.models.intermediateTable.UserEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserDtoMapper {
    private final UserGroupMapper userGroupMapper;
    public UserDtoMapper(UserGroupMapper userGroupMapper) {
        this.userGroupMapper = userGroupMapper;
    }

    public UserWithGroupsDto convertToUserWithGroups(User user){
        return new UserWithGroupsDto(
                user.getId(),
                user.getUId(),
                user.getName(),
                user.getEmail(),
                user.getCountry(),
                user.getDateOfBirth(),
                user.getProfilePhotoId(),
                userGroupMapper.convertMap(user.getGroups())
        );
    }

    public UserWithAllDetailsDto convertToUserWithAllDetailsDto(User user, List<GroupDetailsDto> groupDetailsDtos){
        return new UserWithAllDetailsDto(
                user.getId(),
                user.getUId(),
                user.getName(),
                user.getEmail(),
                user.getCountry(),
                user.getDateOfBirth(),
                user.getProfilePhotoId(),
                groupDetailsDtos
        );
    }

    public List<UserWithRoleDto> convertList(List<ExpensesUser> expensesUsers){
        return expensesUsers.stream().map(expensesUser ->{
            return new UserWithRoleDto(
                    convertTo(expensesUser.getUser()),
                    expensesUser.getRole()
            );
        }).toList();
    }

    public List<UserWithRoleDto> convertListEvent(List<UserEvent> expensesUsers){
        return expensesUsers.stream().map(expensesUser ->{
            return new UserWithRoleDto(
                    convertTo(expensesUser.getUser()),
                    expensesUser.getRole()
            );
        }).toList();
    }


    public UserDto convert(User user) {
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

    public UserWithRoleDto convert(User user, String role){
        return new UserWithRoleDto(
                convertTo(user),
                role
        );
    }

    public UserDtoUidNameEmail convertTo(User user){
        return new UserDtoUidNameEmail(
                user.getUId(),
                user.getName(),
                user.getEmail(),
                user.getProfilePhotoId()
        );
    }

    public UserWithRoleDto convert(ExpensesUser user){
        return new UserWithRoleDto(
                convertTo(user.getUser()),
                user.getRole()
        );
    }


    public List<UserDto> convert(List<User> users) {
        return users.stream()
                .map(this::convert)
                .toList();
    }


    public User convert(UserDto userDto){
        return new User(
                userDto.UId(),
                userDto.userName(),
                userDto.email(),
                userDto.country(),
                userDto.dateOfBirth(),
                userDto.profilePhotoId()
        );
    }

    public User convert(CreateUserDto userDto){
        return new User(
                userDto.UId(),
                userDto.userName(),
                userDto.email(),
                userDto.country(),
                userDto.dateOfBirth(),
                userDto.profilePhotoId()
        );
    }



}
