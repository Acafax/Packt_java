package org.example.springprojektzespolowy.services;

import jakarta.transaction.Transactional;
import org.example.springprojektzespolowy.dto.event.EventDto;
import org.example.springprojektzespolowy.dto.groupDto.GroupDto;
import org.example.springprojektzespolowy.dto.photo.PhotoDto;
import org.example.springprojektzespolowy.dto.userDto.UserDto;
import org.example.springprojektzespolowy.services.userServices.UserEventService;
import org.example.springprojektzespolowy.services.userServices.UserGroupServices;
import org.example.springprojektzespolowy.services.userServices.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class DeleteEntityService {

    private final DocumentService documentService;
    private final GroupService groupService;
    private final EventService eventService;
    private final UserGroupServices userGroupServices;
    private final UserService userService;
    private final UserEventService userEventService;
    private final PhotoService photoService;
    private final InvitationService invitationService;


    public DeleteEntityService(DocumentService ticketService, GroupService groupService, EventService eventService, UserGroupServices userGroupServices, UserService userService, UserEventService userEventService, PhotoService photoService, InvitationService invitationService) {
        this.documentService = ticketService;
        this.groupService = groupService;
        this.eventService = eventService;
        this.userGroupServices = userGroupServices;
        this.userService = userService;
        this.userEventService = userEventService;
        this.photoService = photoService;
        this.invitationService = invitationService;
    }

    @PreAuthorize("@securityService.isGroupAdministrator(authentication.name, #groupId)")
    @Transactional
    public GroupDto deleteGroup(Long groupId){
        GroupDto group = groupService.getGroupDTOById(groupId);


        deleteEventsByGroupId(groupId);
        deletePhotosByGroupId(groupId);
        documentService.deleteDocumentsByGroupId(groupId);
        if (userGroupServices.existByGroupId(groupId)){
            userGroupServices.deleteUserGroupByGroupId(groupId);
        }
        groupService.deleteGroup(groupId);

        return group;
    }

    @PreAuthorize("@securityService.isGroupAdministrator(authentication.name, #groupId)")
    @Transactional
    public EventDto deleteEventById(Long eventId, Long groupId){
        EventDto eventDto = userEventService.deleteUserEventByEventId(eventId);
        eventService.deleteEvent(eventId, groupId);
        return eventDto;
    }

    @Transactional
    public Set<EventDto> deleteEventsByGroupId(Long groupId){
        Set<EventDto> events = eventService.getEventByGroupId(groupId);
        events.forEach(eventDto -> deleteEventById(eventDto.id(), groupId));

        return events;
    }

    @Transactional
    public List<PhotoDto> deletePhotosByGroupId(Long groupId){
        return photoService.deletePhotoByGroupId(groupId);
    }

    @PreAuthorize("@securityService.isRequestingUserisAuthorizedForAccount(authentication.name,#UId)")
    @Transactional
    public UserDto deleteUser(String UId){
        UserDto userDtoById = userService.getUserDtoByUId(UId);

        if (userGroupServices.existByUserId(UId)){
            userGroupServices.deleteUserGroupByUId(UId);
        }
        eventService.deleteMemberWhileDeletingUserByUid(UId);
        invitationService.deleteInvitationByUid(UId);

        userService.deleteUser(UId);

        return userDtoById;
    }
}
