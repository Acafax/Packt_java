package org.example.springprojektzespolowy.controllers;


import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.example.springprojektzespolowy.dto.invitation.InvitationDto;
import org.example.springprojektzespolowy.dto.invitation.InvitationDtoWithoutInviter;
import org.example.springprojektzespolowy.services.InvitationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
        try {
            InvitationDto invitationDto = invitationService.inviteUser(email, groupId);
            return ResponseEntity.ok(invitationDto);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (Exception ex){
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{Uid}")
    public ResponseEntity<List<InvitationDto>> showInvitation(@PathVariable String Uid){
        try {
            List<InvitationDto> invitationDtos = invitationService.showInvitations(Uid);
            return ResponseEntity.ok(invitationDtos);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (Exception ex){
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @DeleteMapping("/{UId}/{groupId}")
    public ResponseEntity<InvitationDtoWithoutInviter> deleteInvitation(@PathVariable String UId, @PathVariable Long groupId){
        try {
            InvitationDtoWithoutInviter invitation = invitationService.deleteInvitation(UId, groupId);
            return ResponseEntity.ok(invitation);

        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }catch (Exception ex){
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
