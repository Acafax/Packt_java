package org.example.springprojektzespolowy.services.userServices;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.springprojektzespolowy.dto.groupDto.CreateGroupDto;
import org.example.springprojektzespolowy.dto.groupDto.GroupDto;
import org.example.springprojektzespolowy.dto.groupDto.GroupWithUsersDto;
import org.example.springprojektzespolowy.dto.mappers.UserDtoMapper;
import org.example.springprojektzespolowy.dto.mappers.UserGroupMapper;
import org.example.springprojektzespolowy.dto.userDto.UserDto;
import org.example.springprojektzespolowy.dto.userDto.UserDtoUidNameEmail;
import org.example.springprojektzespolowy.dto.userEvent.UserWithRoleDto;
import org.example.springprojektzespolowy.dto.userGroupDto.UserGroupDto;
import org.example.springprojektzespolowy.models.Group;
import org.example.springprojektzespolowy.models.User;
import org.example.springprojektzespolowy.models.intermediateTable.UserGroup;
import org.example.springprojektzespolowy.models.intermediateTable.UserGroupKey;
import org.example.springprojektzespolowy.repositories.userRepos.UserGroupRepository;
import org.example.springprojektzespolowy.services.GroupService;
import org.example.springprojektzespolowy.services.InvitationService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserGroupServices {

    private final GroupService groupService;
    private final UserService userService;
    private final UserGroupRepository userGroupRepository;
    private final UserGroupMapper userGroupMapper;
    private final UserDtoMapper userDtoMapper;
    private final InvitationService invitationService;


    public UserGroupServices(GroupService groupService, UserService userService, UserGroupRepository userGroupRepository, UserGroupMapper userGroupMapper, UserDtoMapper userDtoMapper, InvitationService invitationService) {
        this.groupService = groupService;
        this.userService = userService;
        this.userGroupRepository = userGroupRepository;
        this.userGroupMapper = userGroupMapper;
        this.userDtoMapper = userDtoMapper;
        this.invitationService = invitationService;
    }

    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    public GroupWithUsersDto getGroupByIdWithUsers(Long groupId){
        if (!groupService.groupExists(groupId)) throw new EntityNotFoundException("Group not found");

        if (!userGroupRepository.existsUserGroupByGroup_Id(groupId)){
            Group group = groupService.getGroupById(groupId);
            return userGroupMapper.convertWithEmptyMembers(group);
        }

        Group group = groupService.getGroupWithUsersById(groupId);

        Set<UserWithRoleDto> members = group.getUsers()
                .stream().map(userGroup -> {
                    UserDtoUidNameEmail user = userDtoMapper.convertTo(userGroup.getUser());
                    String role = userGroup.getRole();
                    return new UserWithRoleDto(user, role);
                }).collect(Collectors.toSet());

        return userGroupMapper.convert(group,members);
    }

    @Transactional
    public GroupDto createGroupAndAddUserAsAdmin(CreateGroupDto createGroupDto){
        String Uid = SecurityContextHolder.getContext().getAuthentication().getName();

        GroupDto group = groupService.createGroup(createGroupDto);
        addUserToGroupAsAdmin(Uid, group.id());
        createGroupDto.guestEmails()
                    .forEach(quest -> invitationService.inviteUser(quest, group.id()));

        return group;
    }

    public UserGroupDto addUserToGroupAsAdmin(String UId, Long groupId){
        if (!userService.userExistsByUId(UId)) throw new EntityNotFoundException("User id:"+UId+" not foud");
        if (!groupService.groupExists(groupId)) throw new EntityNotFoundException("Group id:"+groupId+" not foud");

        Group group = groupService.getGroupById(groupId);
        User user = userService.getUserByUId(UId);

        UserGroupKey key = new UserGroupKey(user.getId(), groupId);

        if(userGroupRepository.existsById(key)) throw new EntityExistsException("User id:"+ UId+" already exists in group "+groupId);
        UserGroup userGroup = userGroupRepository.save(new UserGroup(key, user, group, "ADMIN"));

        return userGroupMapper.convert(userGroup);
    }

    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    public UserGroupDto addUserToGroup(String UId, Long groupId){
        if (!userService.userExistsByUId(UId)) throw new EntityNotFoundException("User id:"+UId+" not foud");
        if (!groupService.groupExists(groupId)) throw new EntityNotFoundException("Group id:"+groupId+" not foud");

        Group group = groupService.getGroupById(groupId);
        User user = userService.getUserByUId(UId);

        UserGroupKey key = new UserGroupKey(user.getId(), groupId);
        if(userGroupRepository.existsById(key)) throw new EntityExistsException("User id:"+ UId+" already exists in group "+groupId);
        UserGroup userGroup = userGroupRepository.save(new UserGroup(key, user, group, "USER"));

        return userGroupMapper.convert(userGroup);
    }

    @PreAuthorize("@securityService.isGroupAdministrator(authentication.name, #groupId)")
    public UserGroupDto makeUserAnAdministrator(String UId, Long groupId){
        if (!userService.userExistsByUId(UId)) throw new EntityNotFoundException("User id:"+UId+" not foud");
        if (!groupService.groupExists(groupId)) throw new EntityNotFoundException("Group id:"+groupId+" not foud");

        UserDto userDto = userService.getUserDtoByUId(UId);
        UserGroupKey id = new UserGroupKey(userDto.id(), groupId);
       return userGroupRepository.findById(id).map(userGroup -> {
            userGroup.setRole("ADMIN");
            UserGroup save = userGroupRepository.save(userGroup);
            return userGroupMapper.convert(save);
        }).orElseThrow(EntityExistsException::new);

    }

//    public void checkIfUserIsSingleAdmin(String UId, Long groupId){
//        Group group = groupService.getGroupById(groupId);
//        int groupSize = group.getUsers().size();
//        group.getUsers().forEach(userGroup -> userGroup.getUser().getUId());
//        if (groupService.getGroupById(groupId).getUsers().size()==1 &&)
//    }

    public UserDto deleteUserGroupByUId(String userUId){
        if (!userGroupRepository.existsUserGroupByUser_UId(userUId)) throw new EntityNotFoundException("UserGroup not found");

        UserDto userDto = userService.getUserDtoByUId(userUId);
        userGroupRepository.deleteUserGroupByUser_UId(userUId);

        return userDto;
    }

    public GroupDto deleteUserGroupByGroupId(Long groupId){
        if (!userGroupRepository.existsUserGroupByGroup_Id(groupId)) throw new EntityNotFoundException("UserGroup not found");

        GroupDto groupDto = groupService.getGroupDTOById(groupId);
        userGroupRepository.deleteUserGroupByGroup_Id(groupId);

        return groupDto;
    }

    public Boolean existByGroupId(Long groupId){
        if (userGroupRepository.existsUserGroupByGroup_Id(groupId)) return true;
        return false;
    }
    public Boolean existByUserId(String userUId){
        if (userGroupRepository.existsUserGroupByUser_UId(userUId)) return true;
        return false;
    }

}
