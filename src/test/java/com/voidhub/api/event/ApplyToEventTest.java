package com.voidhub.api.event;

import com.voidhub.api.BaseTest;
import com.voidhub.api.entity.*;
import com.voidhub.api.form.EventApplicationForm;
import com.voidhub.api.repository.EventApplicationRepository;
import com.voidhub.api.repository.EventRepository;
import com.voidhub.api.util.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;

public class ApplyToEventTest extends BaseTest {

    private @Autowired UserUtil userUtil;
    private @Autowired EventUtil eventUtil;
    private @Autowired EventRepository eventRepository;
    private @Autowired EventApplicationRepository eventApplicationRepository;

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

        Event queriedEvent = eventRepository.findById(event.getId()).orElseThrow();

        Assertions.assertEquals(
                Role.values().length,
                eventApplicationRepository.getEventApplicationsByEvent_Id(queriedEvent.getId()).size()
        );
    }

    @Test
    public void unauthenticatedUserCanApply() {
        TestUser publisher = userUtil.getUsersWithAuthority("event:write", port).get(0);
        Event event = eventUtil.createAndSaveEvent(publisher.user());

        RestAssured.given()
                .contentType("application/json")
                .body(Util.toBody(
                        new EventApplicationForm("John@Gmail.com", "Doe#0001", "Synthesyzer"))
                )
                .post("api/v1/events/" + event.getId() + "/apply_without_account")
                .then()
                .statusCode(200)
                .body("message", equalTo(
                                "Successfully applied to event. Please confirm" +
                                        " your application by checking your email"
                        )
                );

        Event queriedEvent = eventRepository.findById(event.getId()).orElseThrow();

        Assertions.assertEquals(
                1,
                eventApplicationRepository.getEventApplicationsByEvent_Id(queriedEvent.getId()).size()
        );
    }

    @Test
    public void adminCanReadApplications() {
        TestUser publisher = userUtil.getUsersWithAuthority("event:write", port).get(0);
        Event event = eventUtil.createAndSaveEvent(publisher.user());

        TestUser admin = userUtil.getUsersWithAuthority("event:write", port).get(0);

        for (TestUser user : userUtil.getUsersWithAnyRole(port)) {
            RestAssured.given()
                    .header(user.getAuthHeader())
                    .post("api/v1/events/" + event.getId() + "/apply")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("Successfully applied to event"));
        }

        RestAssured.given()
                .header(admin.getAuthHeader())
                .get("api/v1/events/" + event.getId() + "/applications")
                .then()
                .statusCode(200)
                .body("size()", is(Role.values().length));
    }


}
