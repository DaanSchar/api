package com.voidhub.api.health;

import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class HealthWebLayerTest {

    @Value(value="${local.server.port}")
    private int port;

    @Test
    public void healthReturnsOk() {
        RestAssured.get("http://localhost:" + port + "/health").then().statusCode(200);
    }

}
