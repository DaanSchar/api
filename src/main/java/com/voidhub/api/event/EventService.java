package com.voidhub.api.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getEvents() {
        return eventRepository.findAll(Sort.by("startingDate"));
    }

    public Optional<Event> getEvent(UUID eventId) {
        return eventRepository.findById(eventId);
    }

    public void createNewEvent(Event event) {
        if (event.getCreationDate() == null) {
            event.setCreationDate(new Date());
        }

        event.setApplicationDeadline(new Date());
        event.setStartingDate(new Date());

        eventRepository.save(event);
    }

    //TODO: Horrible solution, fix it
    // The issue is that the event object is not updated in the database
    // if there is a value missing in th event input object.
    public void updateExistingEvent(Event event, UUID eventId) {
        event.setId(eventId);
        eventRepository.findById(eventId).ifPresentOrElse(
                e -> {
                    if (event.getCreationDate() == null) {
                        event.setCreationDate(e.getCreationDate());
                    }
                    if (event.getApplicationDeadline() == null) {
                        event.setApplicationDeadline(e.getApplicationDeadline());
                    }
                    if (event.getStartingDate() == null) {
                        event.setStartingDate(e.getStartingDate());
                    }
                    if (event.getCreator() == null) {
                        event.setCreator(e.getCreator());
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

    public void deleteEvent(UUID eventId) {
        eventRepository.deleteById(eventId);
    }
}
