package com.voidhub.api.repository;

import com.voidhub.api.configuration.file.FileSystemConfig;
import com.voidhub.api.entity.*;
import com.voidhub.api.service.EventApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Component
public class DataSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private @Autowired UserRepository userRepository;
    private @Autowired FileSystemConfig fileSystemConfig;
    private @Autowired EventRepository eventRepository;
    private @Autowired FileRepository fileRepository;
    private @Autowired PasswordEncoder passwordEncoder;
    private @Autowired EventApplicationService eventApplicationService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent e) {
//        User admin = userRepository.saveAndFlush(User.builder()
//                .role(Role.ADMIN)
//                .username("admin")
//                .password(passwordEncoder.encode("password"))
//                .userInfo(new UserInfo("admin@gmail.com", "Admin#0000", "Admin"))
//                .build()
//        );
//
//        User member = userRepository.saveAndFlush(User.builder()
//                .role(Role.MEMBER)
//                .username("member")
//                .password(passwordEncoder.encode("password"))
//                .userInfo(new UserInfo("member@gmail.com", "Member#0000", "Member"))
//                .build()
//        );
//
//        FileData image = fileRepository.saveAndFlush(FileData.builder()
//                .filePath(fileSystemConfig.getPath() + "/1672190960447_ue-1.jpg")
//                .type("image/jpeg")
//                .name("ue-1.jpg")
//                .build());
//
//        Event event = eventRepository.saveAndFlush(Event.builder()
//                .createdAt(new Date())
//                .applicationDeadline(new Date())
//                .startingDate(new Date())
//                .title("Apocalypse")
//                .shortDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque mattis diam ut lacus gravida, eu feugiat erat luctus. Praesent velit lacus, gravida in vestibulum eu, consectetur nec magna. Donec odio nibh, posuere quis vestibulum quis, malesuada id libero. Donec efficitur nisi eu facilisis blandit. Suspendisse congue lacinia pulvinar. Suspendisse porta nisi sodales justo dignissim, ac aliquet neque faucibu.")
//                .fullDescription("Full description")
//                .publishedBy(admin)
//                .image(image)
//                .build()
//        );
//
//        eventRepository.saveAndFlush(Event.builder()
//                .createdAt(new Date())
//                .applicationDeadline(new Date())
//                .startingDate(new Date())
//                .title("Apocalypse")
//                .shortDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque mattis diam ut lacus gravida, eu feugiat erat luctus. Praesent velit lacus, gravida in vestibulum eu, consectetur nec magna. Donec odio nibh, posuere quis vestibulum quis, malesuada id libero. Donec efficitur nisi eu facilisis blandit. Suspendisse congue lacinia pulvinar. Suspendisse porta nisi sodales justo dignissim, ac aliquet neque faucibu.")
//                .fullDescription("Full description")
//                .publishedBy(admin)
//                .image(image)
//                .build()
//        );
//
//        eventRepository.saveAndFlush(Event.builder()
//                .createdAt(new Date())
//                .applicationDeadline(new Date())
//                .startingDate(new Date())
//                .title("Apocalypse")
//                .shortDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque mattis diam ut lacus gravida, eu feugiat erat luctus. Praesent velit lacus, gravida in vestibulum eu, consectetur nec magna. Donec odio nibh, posuere quis vestibulum quis, malesuada id libero. Donec efficitur nisi eu facilisis blandit. Suspendisse congue lacinia pulvinar. Suspendisse porta nisi sodales justo dignissim, ac aliquet neque faucibu.")
//                .fullDescription("Full description")
//                .publishedBy(admin)
//                .image(image)
//                .build()
//        );
//
//        event = eventRepository.findById(event.getId()).get();
//
//        Set<UserInfo> applications = event.getApplications();
//        applications.add(admin.getUserInfo());
//        applications.add(member.getUserInfo());
//        event.setApplications(applications);
//
//        eventRepository.saveAndFlush(event);
    }
}
