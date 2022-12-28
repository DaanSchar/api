package com.voidhub.api.repository;

import com.voidhub.api.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

//    @Query("SELECT COUNT(event_id) as verified_applications from events_userinfos join user_info ui on ui.id = events_userinfos.userinfo_id where ui.is_verified = false and event_id = ?1 group by event_id;")
//    int getVerifiedApplications(UUID eventId);

}
