package com.voidhub.api.file;

import com.voidhub.api.BaseTest;
import com.voidhub.api.entity.FileData;
import com.voidhub.api.repository.FileRepository;
import com.voidhub.api.util.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.UUID;

public class GetFileTest extends BaseTest {

    private @Autowired FileRepository fileRepository;
    private @Autowired UserUtil userUtil;
    private @Autowired FileSystemUtil fileSystemUtil;

    private final String fileWriteAuth = "file:write";

    @AfterEach
    public void afterEach() throws IOException {
        fileSystemUtil.clear();
    }

    @Test
    public void anyoneCanGetFile() {
        TestUser uploader = userUtil.getUserWithAuthority(fileWriteAuth, port);

        RestAssured.given()
                .header("Authorization", uploader.token())
                .multiPart("image", fileSystemUtil.getTestImg(), "image/jpeg")
                .post("/api/v1/images")
                .then()
                .statusCode(201);

        FileData fileData = fileRepository.findAll().get(0);

        RestAssured
                .get("/api/v1/files/" + fileData.getId())
                .then()
                .statusCode(200);
    }

    @Test
    public void getNonExistingFile() {
        RestAssured
                .get("/api/v1/files/" + UUID.randomUUID())
                .then()
                .statusCode(404);
    }

}
