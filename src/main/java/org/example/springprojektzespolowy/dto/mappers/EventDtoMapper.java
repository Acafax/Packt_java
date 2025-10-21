package org.example.springprojektzespolowy.dto.mappers;

import org.example.springprojektzespolowy.dto.event.CreateEventDto;
import org.example.springprojektzespolowy.dto.event.EventBudgetDto;
import org.example.springprojektzespolowy.dto.event.EventDto;
import org.example.springprojektzespolowy.dto.userEvent.UserEventDto;
import org.example.springprojektzespolowy.dto.userEvent.UserWithRoleDto;
import org.example.springprojektzespolowy.models.Event;
import org.example.springprojektzespolowy.models.Group;
import org.example.springprojektzespolowy.models.intermediateTable.ExpensesEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventDtoMapper {

    private final UserDtoMapper userDtoMapper;

    public EventDtoMapper(UserDtoMapper userDtoMapper) {
        this.userDtoMapper = userDtoMapper;
    }

    public EventDto convert(Event event){
        return new EventDto(
                event.getId(),
                event.getName(),
                event.getCategory(),
                event.getDescription(),
                event.getLocalization(),
                event.getEndEvent(),
                event.getStartEvent(),
                event.getCreator(),
                userDtoMapper.convertListEvent(event.getUsers())
        );
    }

    public Set<EventDto> convert(Set<Event> events){
        return events.stream()
                .map(this::convert)
                .collect(Collectors.toSet());

    }

    public Event convert(CreateEventDto event, Group group){
        return  new Event(
                event.eventName(),
                event.category(),
                event.description(),
                event.localization(),
                event.startEvent(),
                event.endEvent(),
                event.creator(),
                group
        );
    }

    public EventBudgetDto conver(UserEventDto eventDto){
        return new EventBudgetDto(
                eventDto.event().id(),
                eventDto.event().eventName(),
                eventDto.event().category(),
                eventDto.event().localization(),
                eventDto.event().description(),
                eventDto.event().startEvent(),
                eventDto.event().endEvent(),
                eventDto.members()
        );
    }

    public Set<EventBudgetDto> convertSet(Set<UserEventDto> userEventDtos){
        return userEventDtos.stream()
                .map(this::conver)
                .collect(Collectors.toSet());
    }

    public EventDto convert(Event event, List<UserWithRoleDto> participants){
        return new EventDto(
                event.getId(),
                event.getName(),
                event.getCategory(),
                event.getDescription(),
                event.getLocalization(),
                event.getStartEvent(),
                event.getEndEvent(),
                event.getCreator(),
                participants
        );
    }

    public Set<EventDto> convertExpEvent(Set<ExpensesEvent> expensesEvents){
        return expensesEvents.stream()
                .map(expensesEvent -> {
            return new EventDto(
                    expensesEvent.getEvent().getId(),
                    expensesEvent.getEvent().getName(),
                    expensesEvent.getEvent().getCategory(),
                    expensesEvent.getEvent().getDescription(),
                    expensesEvent.getEvent().getLocalization(),
                    expensesEvent.getEvent().getStartEvent(),
                    expensesEvent.getEvent().getEndEvent(),
                    expensesEvent.getEvent().getCreator(),
                    userDtoMapper.convertListEvent(expensesEvent.getEvent().getUsers())
            );
        }).collect(Collectors.toSet());
    }

}
