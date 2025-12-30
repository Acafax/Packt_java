package org.example.springprojektzespolowy.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.example.springprojektzespolowy.dto.userGroupDto.UserGroupDto;
import org.example.springprojektzespolowy.services.InvitationService;
import org.example.springprojektzespolowy.services.userServices.UserGroupServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserGroupController {

    private static final Logger log = LoggerFactory.getLogger(UserGroupController.class);
    private final UserGroupServices userGroupServices;
    private final InvitationService invitationService;


    public UserGroupController(UserGroupServices userGroupServices, InvitationService invitationService) {
        this.userGroupServices = userGroupServices;
        this.invitationService = invitationService;
    }

    @PostMapping("addUserToGroup/{UId}/{groupId}")
    public ResponseEntity<UserGroupDto> addUserToGroup(@PathVariable String UId, @PathVariable Long groupId){
        if (!invitationService.userHasInvitation(UId, groupId)) {
            log.warn("User nie posiada zaproszenia");
            throw new EntityNotFoundException("Invitation not found");
        }

        UserGroupDto userGroupDto = userGroupServices.addUserToGroup(UId, groupId);
        invitationService.deleteInvitation(UId, groupId);
        return ResponseEntity.ok(userGroupDto);
    }

    @PatchMapping("patchRole/{UId}/{groupId}")
    public ResponseEntity<UserGroupDto> makeUserAnAdministrator(@PathVariable String UId, @PathVariable Long groupId){
        UserGroupDto userGroupDto = userGroupServices.makeUserAnAdministrator(UId, groupId);
        return ResponseEntity.ok(userGroupDto);
    }
}
