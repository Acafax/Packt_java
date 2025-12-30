package org.example.springprojektzespolowy.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.example.springprojektzespolowy.dto.event.CreateEventDto;
import org.example.springprojektzespolowy.dto.event.EventDto;
import org.example.springprojektzespolowy.dto.event.UpdateEventDto;
import org.example.springprojektzespolowy.dto.mappers.EventDtoMapper;
import org.example.springprojektzespolowy.dto.mappers.GroupDtoMapper;
import org.example.springprojektzespolowy.dto.userEvent.UserEventDto;
import org.example.springprojektzespolowy.dto.userEvent.UserWithRoleDto;
import org.example.springprojektzespolowy.models.Event;
import org.example.springprojektzespolowy.models.Group;
import org.example.springprojektzespolowy.repositories.EventRepository;
import org.example.springprojektzespolowy.repositories.userRepos.UserEventRepository;
import org.example.springprojektzespolowy.services.userServices.UserEventService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class EventCreateService {

    private final UserEventService userEventService;
    private final GroupDtoMapper groupDtoMapper;
    private final EventDtoMapper eventDtoMapper;
    private final EventRepository eventRepository;
    private final GroupService groupService;
    private final UserEventRepository userEventRepository;

    public EventCreateService(UserEventService userEventService, GroupDtoMapper groupDtoMapper, EventDtoMapper eventDtoMapper, EventRepository eventRepository, GroupService groupService, UserEventRepository userEventRepository) {
        this.userEventService = userEventService;
        this.groupDtoMapper = groupDtoMapper;
        this.eventDtoMapper = eventDtoMapper;
        this.eventRepository = eventRepository;
        this.groupService = groupService;
        this.userEventRepository = userEventRepository;
    }

    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    @Transactional
    public EventDto createEvent (CreateEventDto createEvent, Long groupId) throws BadRequestException {
        String creatorUId = SecurityContextHolder.getContext().getAuthentication().getName();
            Group group = groupDtoMapper.convert(groupService.getGroupDTOById(groupId));
            Event event = eventDtoMapper.convert(createEvent, group);
            event.setCreator(creatorUId);
            eventRepository.save(event);

        List<UserWithRoleDto> participants = new ArrayList<>();

        UserWithRoleDto userWithRoleDto = userEventService.addEventCreator(event, creatorUId);
        participants.add(userWithRoleDto);
        createEvent.participants().forEach(participantUId ->{
            if (!participantUId.equals(creatorUId)){
                UserWithRoleDto userWithRole = userEventService.addEventMember(event.getId(), participantUId, groupId);
                participants.add(userWithRole);
            }
        });
        return eventDtoMapper.convert(event, participants);
    }

    @Transactional
    @PreAuthorize("@securityService.isGroupAdministrator(authentication.name, #groupId)")
    public UserEventDto addMemberToEvent(String UId, Long eventId, Long groupId){
        UserWithRoleDto userWithRoleDto = userEventService.addEventMember(eventId, UId, groupId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event not found"));
        EventDto convert = eventDtoMapper.convert(event);
        return new UserEventDto(convert,Set.of(userWithRoleDto));
    }

    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    @Transactional
    public EventDto updateWholeEvent(UpdateEventDto updateEventDto, Long groupId) throws BadRequestException {
        if (!updateEventDto.participants().contains(updateEventDto.creator())) throw new BadRequestException();
        return eventRepository.findById(updateEventDto.id())
                .map(existingEvent ->{
                    existingEvent.setName(updateEventDto.eventName());
                    existingEvent.setCategory(updateEventDto.category());
                    existingEvent.setDescription(updateEventDto.description());
                    existingEvent.setStartEvent(updateEventDto.startEvent());
                    existingEvent.setEndEvent(updateEventDto.endEvent());
                    existingEvent.setLocalization(updateEventDto.localization());

                    userEventRepository.deleteUserEventByEvent_Id(existingEvent.getId());

                    List<UserWithRoleDto> updatedParticipants = new ArrayList<>();
                    updateEventDto.participants().forEach(participant -> {
                        if (updateEventDto.creator().equals(participant)){
                            UserWithRoleDto userWithRoleDto = userEventService.addEventCreator(existingEvent, participant);
                            updatedParticipants.add(userWithRoleDto);
                        }else {
                            UserWithRoleDto userWithRoleDto = userEventService.addEventMember(existingEvent.getId(), participant, groupId);
                            updatedParticipants.add(userWithRoleDto);
                        }
                    });

                    eventRepository.save(existingEvent);
                    return eventDtoMapper.convert(existingEvent, updatedParticipants);
                })
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
    }
}
