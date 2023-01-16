package com.voidhub.api.entity;

import com.voidhub.api.form.create.CreateEventForm;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Event {

    //TODO Add modpack

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String shortDescription;

    @Column(nullable = false, length = 10000)
    private String fullDescription;

    @Column(nullable = false)
    private Date applicationDeadline;

    @Column(nullable = false)
    private Date startingDate;

    @Column(nullable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private User publishedBy;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private FileData image;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "events_userinfos",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "userinfo_id"))
    private Set<UserInfo> applications;

    public Event() {
    }

    public Event(CreateEventForm form, User publishedBy, FileData image) {
        this.title = form.getTitle();
        this.shortDescription = form.getShortDescription();
        this.fullDescription = form.getFullDescription();
        this.applicationDeadline = form.getApplicationDeadline();
        this.startingDate = form.getStartingDate();
        this.publishedBy = publishedBy;
        this.image = image;
    }

}
