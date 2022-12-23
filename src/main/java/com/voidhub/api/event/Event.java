package com.voidhub.api.event;

import com.voidhub.api.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
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
    private Date creationDate;

    @Column(nullable = false)
    private Date applicationDeadline;

    @Column(nullable = false)
    private Date startingDate;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private User creator;

    public Event() {}

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", shortDesc=" + shortDescription +
                ", fullDesc=" + fullDescription +
                ", creationDate=" + creationDate +
                ", applicationDeadline=" + applicationDeadline +
                ", startingDate=" + startingDate +
                ", creator=" + creator + "}";
    }
}
