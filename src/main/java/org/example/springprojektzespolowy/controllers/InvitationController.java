package org.example.springprojektzespolowy.controllers;


import lombok.extern.slf4j.Slf4j;
import org.example.springprojektzespolowy.dto.invitation.InvitationDto;
import org.example.springprojektzespolowy.dto.invitation.InvitationDtoWithoutInviter;
import org.example.springprojektzespolowy.services.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/invitation")
public class InvitationController {
    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }


    @PostMapping("/invite/{email}/{groupId}")
    public ResponseEntity<InvitationDto> inviteUser(@PathVariable String email, @PathVariable Long groupId){
        InvitationDto invitationDto = invitationService.inviteUser(email, groupId);
        return ResponseEntity.ok(invitationDto);
    }

    @GetMapping("/{Uid}")
    public ResponseEntity<List<InvitationDto>> showInvitation(@PathVariable String Uid){
        List<InvitationDto> invitationDtos = invitationService.showInvitations(Uid);
        return ResponseEntity.ok(invitationDtos);
    }

    @DeleteMapping("/{UId}/{groupId}")
    public ResponseEntity<InvitationDtoWithoutInviter> deleteInvitation(@PathVariable String UId, @PathVariable Long groupId){
        InvitationDtoWithoutInviter invitation = invitationService.deleteInvitation(UId, groupId);
        return ResponseEntity.ok(invitation);
    }

}
