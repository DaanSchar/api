package com.voidhub.api.service;

import com.voidhub.api.entity.Event;
import com.voidhub.api.entity.User;
import com.voidhub.api.entity.UserInfo;
import com.voidhub.api.form.EventApplicationForm;
import com.voidhub.api.repository.EventRepository;
import com.voidhub.api.repository.UserRepository;
import com.voidhub.api.util.Message;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class EventApplicationService {

    private @Autowired EventRepository eventRepository;
    private @Autowired UserRepository userRepository;

    public ResponseEntity<Message> apply(UUID eventId, String username) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event does not exist"));

        User user = userRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        Set<UserInfo> applications = event.getApplications();
        applications.add(user.getUserInfo());
        event.setApplications(applications);

        eventRepository.save(event);

        return ResponseEntity.ok(new Message("Successfully applied to event"));
    }

    public ResponseEntity<Message> apply(UUID eventId, EventApplicationForm form) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event does not exist"));

        Set<UserInfo> applications = event.getApplications();

        applications.add(new UserInfo(form));
        event.setApplications(applications);

        eventRepository.save(event);

        return ResponseEntity.ok(new Message("Successfully applied to event. Please confirm your application by checking your email"));
    }

}
