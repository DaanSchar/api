package com.voidhub.api.event;

import com.voidhub.api.util.Message;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventDto> getEvents() {
        return eventService.getEvents();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('event:write')")
    public ResponseEntity<Message> createNewEvent(@Valid @RequestBody NewEventForm form, Principal principal) throws URISyntaxException {
        return eventService.createNewEvent(form, principal.getName());
    }

    @PutMapping("{eventId}")
    //    @PreAuthorize(ADMIN)
    public void updateExistingEvent(@RequestBody Event event, @PathVariable(name = "eventId") UUID eventId) {
        eventService.updateExistingEvent(event, eventId);
    }

    @GetMapping("{eventId}")
    public EventDto getEvent(@PathVariable(name = "eventId") UUID eventId) {
        return eventService.getEvent(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    @DeleteMapping("{eventId}")
    //    @PreAuthorize(ADMIN)
    public void deleteEvent(@PathVariable(name = "eventId") UUID eventId) {
        eventService.deleteEvent(eventId);
    }


}
