package com.voidhub.api.file;

import com.voidhub.api.entity.FileData;
import com.voidhub.api.repository.FileRepository;
import com.voidhub.api.util.FileSystemUtil;
import com.voidhub.api.util.TestUser;
import com.voidhub.api.util.UserUtil;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GetFileTest {

    private @Autowired FileRepository fileRepository;
    private @Autowired UserUtil userUtil;
    private @Autowired FileSystemUtil fileSystemUtil;

    @Value("${local.server.port}")
    private int port;

    private final String fileWriteAuth = "file:write";

    private static File testImg;

    @BeforeAll
    public static void beforeAll() {
        RestAssured.baseURI = "http://localhost";
        testImg = new File("src/test/resources/file-upload/test-files/test-img.jpg");
    }

    @BeforeEach
    public void beforeEach() {
        RestAssured.port = port;
        fileRepository.deleteAll();
        userUtil.clearUsers();
    }

    @AfterEach
    public void afterEach() throws IOException {
        fileSystemUtil.clear();
    }

    @Test
    public void anyoneCanGetFile() {
        TestUser uploader = userUtil.getUserWithAuthority(fileWriteAuth, port);

        RestAssured.given()
                .header("Authorization", uploader.getToken())
                .multiPart("image", testImg, "image/jpeg")
                .post("/api/v1/images")
                .then()
                .statusCode(201);

        FileData fileData = fileRepository.findAll().get(0);

        RestAssured
                .get("/api/v1/files/" + fileData.getId())
                .then()
                .statusCode(200);
    }
}
