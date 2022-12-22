package com.voidhub.api.event;

import com.voidhub.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private static final String ADMIN = "hasRole('ADMIN')";
    private static final String MEMBER = "hasRole('MEMBER')";
    private final EventService eventService;
    private final UserService userService;

    @Autowired
    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping
    public List<Event> getEvents() {
        return eventService.getEvents();
    }

    @PostMapping
    @PreAuthorize(MEMBER)
    public void createNewEvent(@RequestBody Event event, Principal principal) {
//        UserDto user = userService.getUserByUsername(principal.getName())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token"));
//        event.setCreator(user);
//        eventService.createNewEvent(event);
    }

    @PutMapping("{eventId}")
    //    @PreAuthorize(ADMIN)
    public void updateExistingEvent(@RequestBody Event event, @PathVariable(name = "eventId") UUID eventId) {
        eventService.updateExistingEvent(event, eventId);
    }

    @GetMapping("{eventId}")
    public Event getEvent(@PathVariable(name = "eventId") UUID eventId) {
        return eventService.getEvent(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    @DeleteMapping("{eventId}")
    //    @PreAuthorize(ADMIN)
    public void deleteEvent(@PathVariable(name = "eventId") UUID eventId) {
        eventService.deleteEvent(eventId);
    }


}
