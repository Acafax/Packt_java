package org.example.springprojektzespolowy.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.example.springprojektzespolowy.dto.invitation.InvitationDto;
import org.example.springprojektzespolowy.dto.mappers.InvitationDtoMapper;
import org.example.springprojektzespolowy.dto.invitation.InvitationDtoWithoutInviter;
import org.example.springprojektzespolowy.dto.userDto.UserDto;
import org.example.springprojektzespolowy.models.Group;
import org.example.springprojektzespolowy.models.User;
import org.example.springprojektzespolowy.models.Invitation;
import org.example.springprojektzespolowy.models.intermediateTable.InvitationKey;
import org.example.springprojektzespolowy.repositories.InvitationRepository;
import org.example.springprojektzespolowy.services.userServices.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvitationService {
    private final InvitationRepository invitationRepository;
    private final GroupService groupService;
    private final UserService userService;
    private final InvitationDtoMapper invitationDtoMapper;
    private final SecurityService securityService;

    public InvitationService(InvitationRepository invitationRepository, GroupService groupService, UserService userService, InvitationDtoMapper invitationDtoMapper, SecurityService securityService) {
        this.invitationRepository = invitationRepository;
        this.groupService = groupService;
        this.userService = userService;
        this.invitationDtoMapper = invitationDtoMapper;
        this.securityService = securityService;
    }

    @PreAuthorize("@securityService.isGroupAdministrator(authentication.name, #groupId)")
    public InvitationDto inviteUser(String email, Long groupId) {
        String inviterUid = SecurityContextHolder.getContext().getAuthentication().getName();

        User userByEmail = userService.getUserByEmail(email);

        if (!groupService.groupExists(groupId)) {
            throw new EntityNotFoundException("Group not found: " + groupId);
        }
        if (userByEmail==null ||!userService.userExistsByUId(userByEmail.getUId())) {
            throw new EntityNotFoundException("User not found: " + email);
        }

        UserDto inviter = userService.getUserDtoByUId(inviterUid);
        Group group = groupService.getGroupById(groupId);

        InvitationKey invitationKey = new InvitationKey(userByEmail.getId(), group.getId());
        Invitation invitation = new Invitation(invitationKey, userByEmail, group, inviterUid);
        invitationRepository.save(invitation);

        return invitationDtoMapper.convert(invitation, inviter);
    }

    @PreAuthorize("@securityService.isRequestingUserisAuthorizedForAccount(authentication.name, #Uid)")
    public List<InvitationDto> showInvitations(String Uid){
        if (!userService.userExistsByUId(Uid)) {
            throw new EntityNotFoundException("User not found");
        }
        List<Invitation> invitationsByUserUId = invitationRepository.getInvitationsByUser_UId(Uid);

        return invitationsByUserUId.stream().map(invitation -> {
            UserDto inviter = userService.getUserDtoByUId(invitation.getInviterUId());
            return invitationDtoMapper.convert(invitation, inviter);
        }).toList();

    }

    public boolean userHasInvitation(String UId, Long groupId){
        return invitationRepository.existsInvitationByGroup_IdAndUser_UId(groupId, UId);
    }

    @Transactional
    @PreAuthorize("@securityService.isRequestingUserisAuthorizedForAccount(authentication.name, #UId)")
    public InvitationDtoWithoutInviter deleteInvitation(String UId, Long groupId) throws EntityNotFoundException {
        if (!groupService.groupExists(groupId)) {
            throw new EntityNotFoundException("Group not found");
        }
        if (!userService.userExistsByUId(UId)) {
            throw new EntityNotFoundException("User not found");
        }

        Invitation invitation = invitationRepository.findByUser_UIdAndGroup_Id(UId, groupId);

        if (invitation == null){
            throw new EntityNotFoundException("Invitation not found");
        }

        invitationRepository.deleteInvitationByUser_UIdAndGroup_Id(UId, groupId);
        return invitationDtoMapper.convert(invitation);


    }

    public void deleteInvitationByUid(String Uid) {
        invitationRepository.deleteByUser_UId(Uid);

    }
}
