package com.voidhub.api.util;

import com.voidhub.api.entity.Event;
import com.voidhub.api.entity.FileData;
import com.voidhub.api.entity.User;
import com.voidhub.api.form.create.CreateEventForm;
import com.voidhub.api.repository.EventRepository;
import com.voidhub.api.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventUtil {

    private @Autowired EventRepository eventRepository;
    private @Autowired FileSystemUtil fileSystemUtil;
    private @Autowired FileRepository fileRepository;

    public void clearEvents() {
        eventRepository.deleteAll();
    }

    public Event createAndSaveEvent(User user) {
        return eventRepository.save(new Event(
                UUID.randomUUID(),
                "title",
                "shortDescription",
                "fullDescription",
                Util.getRandomFutureDate(),
                Util.getRandomFutureDate(),
                Util.getRandomFutureDate(),
                Util.getRandomFutureDate(),
                user,
                uploadTestImage()
        ));
    }

    public CreateEventForm getRandomValidEventForm() {
        return CreateEventForm.builder()
                .title(UUID.randomUUID().toString())
                .shortDescription(UUID.randomUUID().toString())
                .fullDescription(UUID.randomUUID().toString())
                .applicationDeadline(Util.getRandomFutureDate())
                .startingDate(Util.getRandomFutureDate())
                .imageId(uploadTestImage().getId())
                .build();
    }

    public FileData uploadTestImage() {
        return fileRepository.save(new FileData(
                "testImage",
                "image/png",
                fileSystemUtil.getTestImg().getPath()
        ));
    }

}
