package com.voidhub.api.event;

import com.voidhub.api.BaseTest;
import com.voidhub.api.entity.*;
import com.voidhub.api.form.EventApplicationForm;
import com.voidhub.api.repository.EventRepository;
import com.voidhub.api.util.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.equalTo;

public class ApplyToEventTest extends BaseTest {

    private @Autowired UserUtil userUtil;
    private @Autowired EventUtil eventUtil;
    private @Autowired EventRepository eventRepository;

    @Test
    public void anyAuthenticatedUserCanApply() {
        TestUser publisher = userUtil.getUsersWithAuthority("event:write", port).get(0);
        Event event = eventUtil.createAndSaveEvent(publisher.user());

        for (TestUser user : userUtil.getUsersWithAnyRole(port)) {
            RestAssured.given()
                    .header(user.getAuthHeader())
                    .post("api/v1/events/" + event.getId() + "/apply")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("Successfully applied to event"));
        }

        Event queriedEvent = eventRepository.findById(event.getId()).get();
        Assertions.assertEquals(Role.values().length, queriedEvent.getApplications().size());
    }

    @Test
    public void unauthenticatedUserCanApply() {
        TestUser publisher = userUtil.getUsersWithAuthority("event:write", port).get(0);
        Event event = eventUtil.createAndSaveEvent(publisher.user());

        RestAssured.given()
                .contentType("application/json")
                .body(Util.toBody(
                        new EventApplicationForm("John@Gmail.com", "Doe#0001", "epic_pvper"))
                )
                .post("api/v1/events/" + event.getId() + "/apply_without_account")
                .then()
                .statusCode(200)
                .body("message", equalTo(
                                "Successfully applied to event. Please confirm" +
                                        " your application by checking your email"
                        )
                );

        Event queriedEvent = eventRepository.findById(event.getId()).get();
        Assertions.assertEquals(1, queriedEvent.getApplications().size());
    }


}
