package com.voidhub.api.dto;

import com.voidhub.api.entity.Event;
import com.voidhub.api.entity.UserInfo;
import lombok.Data;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Data
public class EventDto {

    public EventDto(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.shortDescription = event.getShortDescription();
        this.fullDescription = event.getFullDescription();
        this.createdAt = event.getCreatedAt();
        this.applicationDeadline = event.getApplicationDeadline();
        this.startingDate = event.getStartingDate();
        this.publishedBy = new UserDto(event.getPublishedBy());
        this.updatedAt = event.getUpdatedAt();
        this.imageId = event.getImage().getId();
        this.totalApplications = (int) event.getApplications().stream().filter(UserInfo::isVerified).count();
    }


    private String title;
    private UUID id;
    private String shortDescription;
    private String fullDescription;
    private Date createdAt;
    private Date applicationDeadline;
    private Date startingDate;
    private UserDto publishedBy;
    private Date updatedAt;
    private UUID imageId;
    private int totalApplications;

}
