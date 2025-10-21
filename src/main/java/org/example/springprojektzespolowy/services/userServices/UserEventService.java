package org.example.springprojektzespolowy.services.userServices;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.springprojektzespolowy.dto.event.EventDto;
import org.example.springprojektzespolowy.dto.event.UpdateEventDto;
import org.example.springprojektzespolowy.dto.mappers.EventDtoMapper;
import org.example.springprojektzespolowy.dto.mappers.UserDtoMapper;
import org.example.springprojektzespolowy.dto.userEvent.UserEventDto;
import org.example.springprojektzespolowy.dto.userEvent.UserWithRoleDto;
import org.example.springprojektzespolowy.models.Event;
import org.example.springprojektzespolowy.models.User;
import org.example.springprojektzespolowy.models.intermediateTable.UserEvent;
import org.example.springprojektzespolowy.models.intermediateTable.UserEventKey;
import org.example.springprojektzespolowy.repositories.userRepos.UserEventRepository;
import org.example.springprojektzespolowy.services.EventService;
import org.example.springprojektzespolowy.services.SecurityService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserEventService {

    private final UserEventRepository userEventRepository;
    private final UserService userService;
    private final EventService eventService;
    private final EventDtoMapper eventDtoMapper;
    private final UserDtoMapper userDtoMapper;
    private final SecurityService securityService;

    public UserEventService(UserEventRepository userEventRepository, UserService userService, EventService eventService, EventDtoMapper eventDtoMapper, UserDtoMapper userDtoMapper, SecurityService securityService) {
        this.userEventRepository = userEventRepository;
        this.userService = userService;
        this.eventService = eventService;
        this.eventDtoMapper = eventDtoMapper;
        this.userDtoMapper = userDtoMapper;
        this.securityService = securityService;
    }

    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    public UserEventDto getEventWithUsers(Long eventId, Long groupId){
        List<UserEvent> userEventByEventId = userEventRepository.getUserEventByEvent_Id(eventId);
        Event event = userEventByEventId.getFirst().getEvent();
        EventDto eventDto = eventDtoMapper.convert(event);
        Set<UserWithRoleDto> members = new HashSet<>();

        userEventByEventId.forEach(userEvent -> {
            UserWithRoleDto userEventWithoutEventDto = new UserWithRoleDto(
                    userDtoMapper.convertTo(userEvent.getUser()),
                    userEvent.getRole());
            members.add(userEventWithoutEventDto);

        });
        return new UserEventDto(eventDto, members);
    }

    public EventDto deleteUserEventByEventId(Long eventId){
        if (!userEventRepository.existsUserEventByEvent_Id(eventId)) throw new EntityNotFoundException();

        EventDto event = eventService.getEventDtoById(eventId);
        userEventRepository.deleteUserEventByEvent_Id(eventId);

        return event;
    }



    public UserWithRoleDto addEventMember(Long eventId, String UId, Long groupId){
        if (!userService.userExistsByUId(UId)) throw new EntityNotFoundException("User id:"+UId+" not foud");
        if (!eventService.eventExistsByid(eventId)) throw new EntityNotFoundException("Event id:"+eventId+" not foud");
        if(!securityService.isGroupMember(UId,groupId)) throw new AccessDeniedException("User is not a Member");

        Event event = eventService.getEventById(eventId);
        User user = userService.getUserByUId(UId);

        UserEventKey key = new UserEventKey(user.getId(), eventId);

        if(userEventRepository.existsById(key)) throw new EntityExistsException("User id:"+ UId+" already is MEMBER in Event "+eventId);

        UserEvent member = userEventRepository.save(new UserEvent(key, user, event, "MEMBER"));

        return userDtoMapper.convert(member.getUser(),"MEMBER");
    }

    public UserWithRoleDto addEventCreator(Event event, String UId){
        if (!userService.userExistsByUId(UId)) throw new EntityNotFoundException("User id:"+UId+" not foud");

        User user = userService.getUserByUId(UId);

        UserEventKey key = new UserEventKey(user.getId(), event.getId());

        if(userEventRepository.existsById(key)) throw new EntityExistsException("User id:"+ UId+" already is PAYER in Event "+event.getId());

        UserEvent payer = userEventRepository.save(new UserEvent(key, user, event, "CREATOR"));

       return userDtoMapper.convert(payer.getUser(), "CREATOR");
    }


}
