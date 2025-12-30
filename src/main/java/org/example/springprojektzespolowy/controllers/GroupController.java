package org.example.springprojektzespolowy.controllers;

import org.example.springprojektzespolowy.dto.groupDto.CreateGroupDto;
import org.example.springprojektzespolowy.dto.groupDto.GroupDetailsDto;
import org.example.springprojektzespolowy.dto.groupDto.GroupDto;
import org.example.springprojektzespolowy.dto.groupDto.GroupWithUsersDto;
import org.example.springprojektzespolowy.services.DeleteEntityService;
import org.example.springprojektzespolowy.services.GroupService;
import org.example.springprojektzespolowy.services.userServices.UserGroupServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;
    private final DeleteEntityService deleteEntityService;
    private final UserGroupServices userGroupServices;

    public GroupController(GroupService groupService, DeleteEntityService deleteEntityService, UserGroupServices userGroupServices) {
        this.groupService = groupService;
        this.deleteEntityService = deleteEntityService;
        this.userGroupServices = userGroupServices;
    }

    @GetMapping("/all")
    public ResponseEntity<List<GroupDto>> getAllGroups(){
        List<GroupDto> allGroups = groupService.getAllGroups();
        return ResponseEntity.ok(allGroups);
    }

    @GetMapping("/all/with-users/{groupId}")
    public ResponseEntity<GroupWithUsersDto> getGroupByIdWithUsers(@PathVariable Long groupId){
        GroupWithUsersDto group = userGroupServices.getGroupByIdWithUsers(groupId);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable Long id){
        GroupDto groupById = groupService.getGroupDTOById(id);
        return ResponseEntity.ok(groupById);
    }

    @GetMapping("/{groupId}/details")
    public ResponseEntity<GroupDetailsDto> getGroupDetailsById(@PathVariable Long groupId){
        GroupDetailsDto groupById = groupService.getGroupDetailsById(groupId);
        return ResponseEntity.ok(groupById);
    }

    @PostMapping("/create")
    public ResponseEntity<GroupDto> createGroup(@RequestBody CreateGroupDto createGroupDto){
        GroupDto groupDto = userGroupServices.createGroupAndAddUserAsAdmin(createGroupDto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{groupId}")
                .buildAndExpand(groupDto.id())
                .toUri();

        return ResponseEntity.created(uri).body(groupDto);
    }

    @PutMapping("/update")
    public ResponseEntity<GroupDto> updateGroup(@RequestBody GroupDto groupDto){
        GroupDto group = groupService.updateGroup(groupDto);
        return ResponseEntity.ok(group);
    }

    @PatchMapping("/patch/{groupId}")
    public ResponseEntity<GroupDto> patchGroup(@RequestBody GroupDto groupDto, @PathVariable Long groupId){
        GroupDto group = groupService.patchGroup(groupDto, groupId);
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GroupDto> deleteGroup(@PathVariable Long id){
        GroupDto group = deleteEntityService.deleteGroup(id);
        return ResponseEntity.ok(group);
    }

}
