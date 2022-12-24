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

    //TODO: Horrible solution, fix it
    // The issue is that the event object is not updated in the database
    // if there is a value missing in th event input object.
    public void updateExistingEvent(Event event, UUID eventId) {
        event.setId(eventId);
        eventRepository.findById(eventId).ifPresentOrElse(
                e -> {
                    if (event.getCreatedAt() == null) {
                        event.setCreatedAt(e.getCreatedAt());
                    }
                    if (event.getApplicationDeadline() == null) {
                        event.setApplicationDeadline(e.getApplicationDeadline());
                    }
                    if (event.getStartingDate() == null) {
                        event.setStartingDate(e.getStartingDate());
                    }
                    if (event.getPublishedBy() == null) {
                        event.setPublishedBy(e.getPublishedBy());
                    }
                    if (event.getFullDescription() == null) {
                        event.setFullDescription(e.getFullDescription());
                    }
                    if (event.getShortDescription() == null) {
                        event.setShortDescription(e.getShortDescription());
                    }
                },
                () -> {}
        );

        eventRepository.save(event);
    }

    public ResponseEntity<Message> deleteEvent(UUID eventId, String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event does not exist"));

        if (!event.getPublishedBy().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                    .body(new Message("You did not publish this event"));
        }

        eventRepository.deleteById(eventId);

        return ResponseEntity.ok(new Message("Successfully deleted event"));
    }
}
