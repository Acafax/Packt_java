package org.example.springprojektzespolowy.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.springprojektzespolowy.dto.event.EventDto;
import org.example.springprojektzespolowy.dto.event.UpdateEventDto;
import org.example.springprojektzespolowy.dto.mappers.EventDtoMapper;
import org.example.springprojektzespolowy.models.Event;
import org.example.springprojektzespolowy.repositories.EventRepository;
import org.example.springprojektzespolowy.repositories.userRepos.UserEventRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;
    private final GroupService groupService;
    private final UserEventRepository userEventRepository;


    public EventService(EventRepository eventRepository, EventDtoMapper eventDtoMapper, GroupService groupService, UserEventRepository userEventRepository) {
        this.eventRepository = eventRepository;
        this.eventDtoMapper = eventDtoMapper;
        this.groupService = groupService;
        this.userEventRepository = userEventRepository;
    }

    public Event getEventById(Long eventId){
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket with id:" + eventId + " Not found "));
    }
    public EventDto getEventDtoById(Long eventId){
        return eventDtoMapper.convert(getEventById(eventId));
    }

    public Set<EventDto> getEventByGroupId(Long groupId){
        Set<Event> events = eventRepository.findByGroup_Id(groupId);
        return eventDtoMapper.convert(events);
    }

    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    public Set<EventDto> getEventsByNameAndGroupId(String eventName, Long groupId){
        if (!eventRepository.existsEventByName(eventName)) throw new EntityNotFoundException("Event not found");
        if (!groupService.groupExists(groupId)) throw new EntityNotFoundException("Group not found");
        Set<Event> events = eventRepository.getEventsByNameAndGroupId(eventName, groupId);
        return eventDtoMapper.convert(events);
    }

    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    public Set<EventDto> getAllEventsByGroupId(Long groupId){
        if(!groupService.groupExists(groupId)) throw new EntityNotFoundException("Group not found");

        Set<Event> allByGroupId = eventRepository.getEventsByGroupId(groupId);
        return eventDtoMapper.convert(allByGroupId);
    }

    public Boolean eventExistsByid(Long eventId){
        return eventRepository.existsById(eventId);
    }

    @PreAuthorize("@securityService.isGroupAdministrator(authentication.name, #groupId)")
    public void deleteEvent(Long id, Long groupId){
        eventRepository.deleteById(id);
    }



    @PreAuthorize("@securityService.isGroupMember(authentication.name, #groupId)")
    @Transactional
    public EventDto patchEvent(UpdateEventDto updateEventDto, Long eventId, Long groupId){
        return eventRepository.findById(eventId)
                .map(existingEvent ->{
                    if (updateEventDto.eventName()!=null)existingEvent.setName(updateEventDto.eventName());
                    if (updateEventDto.category()!=null)existingEvent.setCategory(updateEventDto.category());
                    if (updateEventDto.description()!=null)existingEvent.setDescription(updateEventDto.description());
                    if (updateEventDto.startEvent()!=null)existingEvent.setStartEvent(updateEventDto.startEvent());
                    if (updateEventDto.endEvent()!=null)existingEvent.setEndEvent(updateEventDto.endEvent());

                    eventRepository.save(existingEvent);

                    return eventDtoMapper.convert(existingEvent);
                })
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
    }

    public void deleteMemberWhileDeletingUserByUid(String Uid){
        userEventRepository.deleteByUser_UId(Uid);
    }

}
