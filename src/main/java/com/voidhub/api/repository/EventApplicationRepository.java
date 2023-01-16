package com.voidhub.api.repository;

import com.voidhub.api.entity.EventApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventApplicationRepository extends JpaRepository<EventApplication, Integer> {

    List<EventApplication> getEventApplicationsByEvent_Id(UUID eventId);

}
