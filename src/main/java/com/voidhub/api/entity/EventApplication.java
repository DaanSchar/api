package com.voidhub.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_applications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private Event event;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private UserInfo userInfo;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean accepted;

}
