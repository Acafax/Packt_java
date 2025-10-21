package org.example.springprojektzespolowy.controllers;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.example.springprojektzespolowy.dto.event.CreateEventDto;
import org.example.springprojektzespolowy.dto.event.EventDto;
import org.example.springprojektzespolowy.dto.event.UpdateEventDto;
import org.example.springprojektzespolowy.dto.userEvent.UserEventDto;
import org.example.springprojektzespolowy.services.DeleteEntityService;
import org.example.springprojektzespolowy.services.EventCreateService;
import org.example.springprojektzespolowy.services.EventService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
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
        try {
            Set<EventDto> allEventsByGroupId = eventService.getAllEventsByGroupId(groupId);
            return ResponseEntity.ok(allEventsByGroupId);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{eventName}/{groupId}")
    public ResponseEntity<Set<EventDto>> getEventByNameAndGroupId(@PathVariable Long groupId, @PathVariable String eventName){
        try {
            Set<EventDto> events = eventService.getEventsByNameAndGroupId(eventName, groupId);
            return ResponseEntity.ok(events);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/create/{groupId}")
    public ResponseEntity<EventDto> createEvent(@RequestBody CreateEventDto createEventsDto, @PathVariable Long groupId){
        try {
            EventDto events = eventCreateService.createEvent(createEventsDto, groupId);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/event/{groupId}")
                    .buildAndExpand(groupId)
                    .toUri();
            return ResponseEntity.created(uri).body(events);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (BadRequestException ex){
            return ResponseEntity.badRequest().build();
        }catch (EntityExistsException | DataIntegrityViolationException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add-member/{eventId}/{UId}/{groupId}")
    public ResponseEntity<UserEventDto> addMember(@PathVariable Long eventId, @PathVariable String UId, @PathVariable Long groupId){
        try {
            UserEventDto userEventDto = eventCreateService.addMemberToEvent(UId, eventId, groupId);
            return ResponseEntity.ok(userEventDto);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (EntityExistsException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PutMapping("/update/{groupId}")
    public ResponseEntity<EventDto> updateEvent(@RequestBody UpdateEventDto updateEventDto, @PathVariable Long groupId){
        try {
            EventDto eventDto = eventCreateService.updateWholeEvent(updateEventDto,groupId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(eventDto);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (BadRequestException ex){
            return ResponseEntity.badRequest().build();
        }
        catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/patch/{id}/{groupId}")
    public ResponseEntity<EventDto> patchEvent(@RequestBody UpdateEventDto updateEventDto, @PathVariable Long id, @PathVariable Long groupId){
        try {
            EventDto eventDto = eventService.patchEvent(updateEventDto, id, groupId);
            return ResponseEntity.ok(eventDto);
        }catch (EntityNotFoundException ex){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @DeleteMapping("/{id}/{groupId}")
    public ResponseEntity<EventDto> deleteEvent(@PathVariable Long id, @PathVariable Long groupId){
        try {
            EventDto event = deleteEntityService.deleteEventById(id, groupId);
            return ResponseEntity.ok(event);
        }catch (EntityNotFoundException | HttpServerErrorException.InternalServerError ex  ){
            return ResponseEntity.notFound().build();
        }catch (AccessDeniedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
