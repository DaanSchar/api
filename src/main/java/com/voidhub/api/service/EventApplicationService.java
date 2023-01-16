package com.voidhub.api.service;

import com.voidhub.api.entity.*;
import com.voidhub.api.form.EventApplicationForm;
import com.voidhub.api.repository.EventApplicationRepository;
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
    private @Autowired EventApplicationRepository eventApplicationRepository;
    private @Autowired UserRepository userRepository;
    private @Autowired MojangService mojangService;

    public ResponseEntity<Message> apply(UUID eventId, String username) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event does not exist"));

        User user = userRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        EventApplication application = EventApplication.builder()
                .userInfo(user.getUserInfo())
                .event(event)
                .build();
        eventApplicationRepository.save(application);

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

        MinecraftUserInfo minecraftUserInfo = mojangService.getMinecraftUserInfo(form.getMinecraftName())
                .orElseThrow(() -> new EntityNotFoundException("Minecraft user does not exist"));

        UserInfo userInfo = new UserInfo(
                form.getEmail(),
                form.getDiscordName(),
                minecraftUserInfo
        );

        EventApplication application = EventApplication.builder()
                .userInfo(userInfo)
                .event(event)
                .build();
        eventApplicationRepository.save(application);

        applications.add(userInfo);
        event.setApplications(applications);
        eventRepository.save(event);

        return ResponseEntity.ok(new Message("Successfully applied to event. Please confirm your application by checking your email"));
    }

}
