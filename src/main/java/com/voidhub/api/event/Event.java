package com.voidhub.api.event;

import com.voidhub.api.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String shortDescription;

    @Column(nullable = false)
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

    public Event() {}

    public Event(NewEventForm form, User publishedBy) {
        this.title = form.getTitle();
        this.shortDescription = form.getShortDescription();
        this.fullDescription = form.getFullDescription();
        this.applicationDeadline = form.getApplicationDeadline();
        this.startingDate = form.getStartingDate();
        this.publishedBy = publishedBy;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", shortDesc=" + shortDescription +
                ", fullDesc=" + fullDescription +
                ", creationDate=" + createdAt +
                ", applicationDeadline=" + applicationDeadline +
                ", startingDate=" + startingDate +
                ", creator=" + publishedBy + "}";
    }
}
