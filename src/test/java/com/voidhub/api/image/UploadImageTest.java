package com.voidhub.api.image;

import com.voidhub.api.entity.*;
import com.voidhub.api.repository.*;
import com.voidhub.api.util.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.*;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UploadImageTest {

//    private @Autowired UserRepository userRepository;
    private @Autowired FileRepository fileRepository;
    private @Autowired UserUtil userUtil;
    private @Autowired FileSystemUtil fileSystemUtil;

    @Value("${local.server.port}")
    private int port;

    private final String fileWriteAuth = "file:write";
    private final String url = "/api/v1/images";
    private static File testImg;
    private static File testTxt;

    @BeforeAll
    public static void beforeAll() {
        RestAssured.baseURI = "http://localhost";

        testImg = new File("src/test/resources/file-upload/test-files/test-img.jpg");
        testTxt = new File("src/test/resources/file-upload/test-files/test-txt.txt");
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
    public void testFileExists() {
        Assertions.assertTrue(testImg.exists());
    }

    @Test
    public void roleWithWriteFileAuthExist() {
        Assertions.assertTrue(Util.getRolesWithAuthority(fileWriteAuth).size() > 0);
    }

    @Test
    public void rolesWithoutWriteFileAuthExist() {
        Assertions.assertTrue(Util.getRolesWithoutAuthority(fileWriteAuth).size() > 0);
    }

    @Test
    public void userWithFileWriteAuthority_UploadImage_ReturnsCreated() {
        for (TestUser testUser : userUtil.getUsersWithAuthority(fileWriteAuth, port)) {
            fileRepository.deleteAll();
            int totalFilesInFileSystemBeforeUpload = fileSystemUtil.getDirectory().list().length;

            var result = RestAssured.given()
                    .header("Authorization", testUser.getToken())
                    .multiPart("image", testImg, "image/jpeg")
                    .post(url)
                    .then()
                    .statusCode(201)
                    .body("message", equalTo("File uploaded successfully"));


            Assertions.assertEquals(1, fileRepository.findAll().size());
            FileData fileData = fileRepository.findAll().get(0);

            result.header("Location", equalTo("/api/v1/files/" + fileData.getId()));

            Assertions.assertEquals(
                    totalFilesInFileSystemBeforeUpload + 1,
                    fileSystemUtil.getDirectory().list().length
            );
        }
    }

    @Test
    public void userWithoutWriteFileAuthority_UploadsFile_Returns401() throws IOException {
        for (TestUser user : userUtil.getUsersWithoutAuthority(fileWriteAuth, port)) {
            fileRepository.deleteAll();
            int totalFilesInFileSystemBeforeUpload = fileSystemUtil.getDirectory().list().length;

            RestAssured.given()
                    .header("Authorization", user.getToken())
                    .multiPart("image", testImg, "image/jpeg")
                    .post(url)
                    .then()
                    .statusCode(401);

            Assertions.assertEquals(0, fileRepository.findAll().size());
            Assertions.assertEquals(
                    totalFilesInFileSystemBeforeUpload,
                    fileSystemUtil.getDirectory().list().length
            );
        }
    }

    @Test
    public void fileIsNotAnImage() {
        TestUser user = userUtil.getUserWithAuthority(fileWriteAuth, port);

        RestAssured.given()
                .header("Authorization", user.getToken())
                .multiPart("image", testTxt, "application/text")
                .post(url)
                .then()
                .statusCode(400)
                .body("message", equalTo("File is not an image"));

        Assertions.assertEquals(0, fileRepository.findAll().size());
    }

}
