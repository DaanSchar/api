package com.voidhub.api.health;

import com.voidhub.api.BaseTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

public class HealthWebLayerTest extends BaseTest {

    @Test
    public void healthReturnsOk() {
        RestAssured.get("http://localhost:" + port + "/health").then().statusCode(200);
    }

}
