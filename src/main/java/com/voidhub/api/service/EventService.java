package com.voidhub.api.service;

import com.voidhub.api.dto.EventDto;
import com.voidhub.api.entity.*;
import com.voidhub.api.form.create.CreateEventForm;
import com.voidhub.api.form.update.UpdateEventForm;
import com.voidhub.api.repository.*;
import com.voidhub.api.util.Message;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.net.*;
import java.util.*;

@Service
public class EventService {

    private @Autowired EventRepository eventRepository;
    private @Autowired FileRepository fileRepository;
    private @Autowired UserRepository userRepository;
//    private @Autowired Environment environment;

    @Value("${server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    public List<EventDto> getEvents() {
        return eventRepository.findAll(Sort.by("startingDate"))
                .stream()
                .map(this::mapToEventDto)
                .toList();
    }

    public Optional<EventDto> getEvent(UUID eventId) {
        return eventRepository.findById(eventId).map(this::mapToEventDto);
    }

    public ResponseEntity<Message> createNewEvent(CreateEventForm form, String username) throws URISyntaxException {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        FileData image = fileRepository.findById(form.getImageId())
                .orElseThrow(() -> new EntityNotFoundException("Image does not exist"));

        Event newEvent = eventRepository.save(new Event(form, user, image));

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

    private EventDto mapToEventDto(Event event) {
        var eventDto = new EventDto(event);
        eventDto.setImage("http://" + serverAddress + ":" + serverPort + "/api/v1/files/" + event.getImage().getId());
        return eventDto;
    }
}
