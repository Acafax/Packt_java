package org.example.springprojektzespolowy.services;


import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.springprojektzespolowy.dto.groupDto.CreateGroupDto;
import org.example.springprojektzespolowy.dto.groupDto.GroupDetailsDto;
import org.example.springprojektzespolowy.dto.groupDto.GroupDto;
import org.example.springprojektzespolowy.dto.mappers.GroupDtoMapper;
import org.example.springprojektzespolowy.dto.mappers.UserDtoMapper;
import org.example.springprojektzespolowy.dto.userDto.UserDto;
import org.example.springprojektzespolowy.models.Group;
import org.example.springprojektzespolowy.models.intermediateTable.UserGroup;
import org.example.springprojektzespolowy.repositories.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GroupService {
    private static final Logger log = LoggerFactory.getLogger(GroupService.class);
    private final GroupRepository groupRepository;
    private final GroupDtoMapper groupDtoMapper;
    private final UserDtoMapper userDtoMapper;

    public GroupService(GroupRepository groupRepository, GroupDtoMapper groupDtoMapper , UserDtoMapper userDtoMapper) {
        this.groupRepository = groupRepository;
        this.groupDtoMapper = groupDtoMapper;
        this.userDtoMapper = userDtoMapper;
    }


    @Transactional
    @PreAuthorize("@securityService.isGroupMember(authentication.name, #id)")
    public GroupDetailsDto getGroupDetailsById(Long id){
        if (!groupExists(id)) throw new EntityNotFoundException("Group not found");

        Group group = groupRepository.findGroupDetailsById(id);
        int numberOfMembers = group.getUsers().size();

        return groupDtoMapper.convertGroupDetailsDto(group, numberOfMembers);

    }

    public Group getGroupById(Long id){
        return groupRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Group not found"));
    }

    public Boolean groupExists(Long id){
        return groupRepository.existsById(id);
    }

    @PreAuthorize("@securityService.isGroupMember(authentication.name, #id)")
    public GroupDto getGroupDTOById(Long id){
        Group group = groupRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Group with id:" + id + " Not found"));
        return groupDtoMapper.convert(group);
    }

    @PreAuthorize("@securityService.isDeveloper(authentication.name)")
    public List<GroupDto> getAllGroups(){
        try {
            List<Group> all = groupRepository.findAll();
            return groupDtoMapper.convert(all);
        }catch (EmptyResultDataAccessException ex){
            throw new EntityNotFoundException("Group not found");
        }
    }

    public GroupDto createGroup(CreateGroupDto createGroupDto){
        try {
            Group createGroup = groupDtoMapper.convert(createGroupDto);
            Group group = groupRepository.save(createGroup);
            return groupDtoMapper.convert(group);
        }catch (DataIntegrityViolationException e){
            throw new EntityExistsException("Group with given variables exist");
        }
    }

    @PreAuthorize("@securityService.isGroupAdministrator(authentication.name, #groupDto.id())")
    public GroupDto updateGroup(GroupDto groupDto){
        return groupRepository.findById(groupDto.id()).map(group -> {
            group.setName(groupDto.groupName());
            group.setDescription(groupDto.description());
            group.setCurrency(groupDto.currency());
            group.setStartDate(groupDto.startDate());
            group.setEndDate(groupDto.endDate());
            group.setMaxBudget(groupDto.maxBudget());

            Group save = groupRepository.save(group);
            return groupDtoMapper.convert(save);
        }).orElseThrow(() -> new EntityNotFoundException("Group not found"));
    }

    @PreAuthorize("@securityService.isGroupAdministrator(authentication.name, #groupId)")
    public GroupDto patchGroup(GroupDto groupDto, Long groupId){
        return groupRepository.findById(groupId).map(group -> {
            if (groupDto.groupName()!=null) group.setName(groupDto.groupName());
            if (groupDto.description()!=null) group.setDescription(groupDto.description());
            if (groupDto.currency()!=null) group.setCurrency(groupDto.currency());
            if (groupDto.startDate()!=null) group.setStartDate(groupDto.startDate());
            if (groupDto.endDate()!=null) group.setEndDate(groupDto.endDate());
            if (groupDto.maxBudget()!=null) group.setMaxBudget(groupDto.maxBudget());

            Group newGroup = groupRepository.save(group);
            return groupDtoMapper.convert(newGroup);
        }).orElseThrow(() -> new EntityNotFoundException("Group not found"));
    }

    public void deleteGroup(Long id){

        groupRepository.deleteById(id);
    }

    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    public Group getGroupWithUsersById(Long groupId){
        if (!groupExists(groupId)) throw new EntityNotFoundException("Group not found");
        return groupRepository.findGroupWithUsersById(groupId);
    }

    public Boolean checkUIdUserIsInGroup(String UId, Long groupId){
        Set<UserGroup> users = getGroupWithUsersById(groupId).getUsers();

        return users.stream()
                .anyMatch(userGroup -> userGroup.getUser().getUId().equals(UId));
    }

    public void patchGroupProfilePhoto(Long groupId, Long newProfilePhotoId) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"))
                .setProfilePhotoId(newProfilePhotoId);
    }
}
