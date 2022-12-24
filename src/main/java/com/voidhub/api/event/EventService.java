package com.voidhub.api.event;

import com.voidhub.api.user.*;
import com.voidhub.api.util.Message;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.net.*;
import java.util.*;

@Service
public class EventService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    @Autowired
    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<EventDto> getEvents() {
        return eventRepository.findAll(Sort.by("startingDate"))
                .stream()
                .map(EventDto::new)
                .toList();
    }

    public Optional<EventDto> getEvent(UUID eventId) {
        return eventRepository.findById(eventId).map(EventDto::new);
    }

    public ResponseEntity<Message> createNewEvent(NewEventForm form, String username) throws URISyntaxException {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));


        Event newEvent = eventRepository.save(
                new Event(form, user)
        );

        return ResponseEntity
                .created(new URI("/api/v1/events/" + newEvent.getId()))
                .body(new Message("Successfully created event"));
    }

    public ResponseEntity<Message> updateExistingEvent(UpdateEventForm form, UUID eventId, String username) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event does not exist"));

        if (!userPublishedThisEvent(event, username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                    .body(new Message("You did not publish this event"));
        }

        if (form.getTitle() != null) {
            event.setTitle(form.getTitle());
        }
        if (form.getShortDescription() != null) {
            event.setShortDescription(form.getShortDescription());
        }
        if (form.getFullDescription() != null) {
            event.setFullDescription(form.getFullDescription());
        }
        if (form.getStartingDate() != null) {
            event.setStartingDate(form.getStartingDate());
        }
        if (form.getApplicationDeadline() != null) {
            event.setApplicationDeadline(form.getApplicationDeadline());
        }

        eventRepository.save(event);

        return ResponseEntity.ok(new Message("Successfully updated event"));
    }

    public ResponseEntity<Message> deleteEvent(UUID eventId, String username) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event does not exist"));

        if (!userPublishedThisEvent(event, username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                    .body(new Message("You did not publish this event"));
        }

        eventRepository.deleteById(eventId);

        return ResponseEntity.ok(new Message("Successfully deleted event"));
    }

    public boolean userPublishedThisEvent(Event event, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        return event.getPublishedBy().equals(user);
    }
}
