package org.example.springprojektzespolowy.controllers;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.example.springprojektzespolowy.dto.event.CreateEventDto;
import org.example.springprojektzespolowy.dto.event.EventDto;
import org.example.springprojektzespolowy.dto.event.UpdateEventDto;
import org.example.springprojektzespolowy.dto.userEvent.UserEventDto;
import org.example.springprojektzespolowy.services.DeleteEntityService;
import org.example.springprojektzespolowy.services.EventCreateService;
import org.example.springprojektzespolowy.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;
    private final EventCreateService eventCreateService;
    private final DeleteEntityService deleteEntityService;

    public EventController(EventService eventService, EventCreateService eventCreateService, DeleteEntityService deleteEntityService) {
        this.eventService = eventService;
        this.eventCreateService = eventCreateService;
        this.deleteEntityService = deleteEntityService;
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<Set<EventDto>> getEventsFromGroupByGroupId(@PathVariable Long groupId){
        Set<EventDto> allEventsByGroupId = eventService.getAllEventsByGroupId(groupId);
        return ResponseEntity.ok(allEventsByGroupId);
    }

    @GetMapping("/{eventName}/{groupId}")
    public ResponseEntity<Set<EventDto>> getEventByNameAndGroupId(@PathVariable Long groupId, @PathVariable String eventName){
        Set<EventDto> events = eventService.getEventsByNameAndGroupId(eventName, groupId);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/create/{groupId}")
    public ResponseEntity<EventDto> createEvent(@RequestBody CreateEventDto createEventsDto, @PathVariable Long groupId) throws BadRequestException {
        EventDto events = eventCreateService.createEvent(createEventsDto, groupId);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/event/{groupId}")
                .buildAndExpand(groupId)
                .toUri();
        return ResponseEntity.created(uri).body(events);
    }

    @PostMapping("/add-member/{eventId}/{UId}/{groupId}")
    public ResponseEntity<UserEventDto> addMember(@PathVariable Long eventId, @PathVariable String UId, @PathVariable Long groupId){
        UserEventDto userEventDto = eventCreateService.addMemberToEvent(UId, eventId, groupId);
        return ResponseEntity.ok(userEventDto);
    }

    @PutMapping("/update/{groupId}")
    public ResponseEntity<EventDto> updateEvent(@RequestBody UpdateEventDto updateEventDto, @PathVariable Long groupId) throws BadRequestException {
        EventDto eventDto = eventCreateService.updateWholeEvent(updateEventDto, groupId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(eventDto);
    }

    @PatchMapping("/patch/{id}/{groupId}")
    public ResponseEntity<EventDto> patchEvent(@RequestBody UpdateEventDto updateEventDto, @PathVariable Long id, @PathVariable Long groupId){
        EventDto eventDto = eventService.patchEvent(updateEventDto, id, groupId);
        return ResponseEntity.ok(eventDto);
    }

    @DeleteMapping("/{id}/{groupId}")
    public ResponseEntity<EventDto> deleteEvent(@PathVariable Long id, @PathVariable Long groupId){
        EventDto event = deleteEntityService.deleteEventById(id, groupId);
        return ResponseEntity.ok(event);
    }

}
