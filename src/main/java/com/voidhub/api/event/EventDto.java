package com.voidhub.api.event;

import com.voidhub.api.user.UserDto;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class EventDto {

    public EventDto(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.shortDescription = event.getShortDescription();
        this.fullDescription = event.getFullDescription();
        this.creationDate = event.getCreatedAt();
        this.applicationDeadline = event.getApplicationDeadline();
        this.startingDate = event.getStartingDate();
        this.creator = new UserDto(event.getPublishedBy());
    }

    private String title;
    private UUID id;
    private String shortDescription;
    private String fullDescription;
    private Date creationDate;
    private Date applicationDeadline;
    private Date startingDate;
    private UserDto creator;

}
