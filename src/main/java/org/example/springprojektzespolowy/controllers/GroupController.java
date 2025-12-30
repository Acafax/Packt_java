package org.example.springprojektzespolowy.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.example.springprojektzespolowy.dto.groupDto.CreateGroupDto;
import org.example.springprojektzespolowy.dto.groupDto.GroupDetailsDto;
import org.example.springprojektzespolowy.dto.groupDto.GroupDto;
import org.example.springprojektzespolowy.dto.groupDto.GroupWithUsersDto;
import org.example.springprojektzespolowy.services.DeleteEntityService;
import org.example.springprojektzespolowy.services.GroupService;
import org.example.springprojektzespolowy.services.userServices.UserGroupServices;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
        try {
            List<GroupDto> allGroups = groupService.getAllGroups();
            return ResponseEntity.ok(allGroups);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all/with-users/{groupId}")
    public ResponseEntity<GroupWithUsersDto> getGroupByIdWithUsers(@PathVariable Long groupId){
        try {
            GroupWithUsersDto group = userGroupServices.getGroupByIdWithUsers(groupId);
            return ResponseEntity.ok(group);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable Long id){
        try {
            GroupDto groupById = groupService.getGroupDTOById(id);
            return ResponseEntity.ok(groupById);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{groupId}/details")
    public ResponseEntity<GroupDetailsDto> getGroupDetailsById(@PathVariable Long groupId){

            GroupDetailsDto groupById = groupService.getGroupDetailsById(groupId);
            return ResponseEntity.ok(groupById);

    }

    @PostMapping("/create")
    public ResponseEntity<GroupDto> createGroup(@RequestBody CreateGroupDto createGroupDto){
        try {
            GroupDto groupDto = userGroupServices.createGroupAndAddUserAsAdmin(createGroupDto);

            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("{groupId}")
                    .buildAndExpand(groupDto.id())
                    .toUri();

            return ResponseEntity.created(uri).body(groupDto);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<GroupDto> updateGroup(@RequestBody GroupDto groupDto){
        try {
            GroupDto group = groupService.updateGroup(groupDto);
            return ResponseEntity.ok(group);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/patch/{groupId}")
    public ResponseEntity<GroupDto> patchGroup(@RequestBody GroupDto groupDto, @PathVariable Long groupId){
        try {
            GroupDto group = groupService.patchGroup(groupDto, groupId);
            return ResponseEntity.ok(group);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GroupDto> deleteGroup(@PathVariable Long id){
        try {
            GroupDto group = deleteEntityService.deleteGroup(id);
            return ResponseEntity.ok(group);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
