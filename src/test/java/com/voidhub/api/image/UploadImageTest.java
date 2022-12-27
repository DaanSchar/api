package com.voidhub.api.image;

import com.voidhub.api.BaseTest;
import com.voidhub.api.entity.*;
import com.voidhub.api.repository.*;
import com.voidhub.api.util.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;

import java.io.*;

import static org.hamcrest.Matchers.equalTo;

public class UploadImageTest extends BaseTest {

    private @Autowired FileRepository fileRepository;
    private @Autowired UserUtil userUtil;
    private @Autowired FileSystemUtil fileSystemUtil;

    private final String fileWriteAuth = "file:write";
    private final String url = "/api/v1/images";


    @AfterEach
    public void afterEach() throws IOException {
        fileSystemUtil.clear();
    }

    @Test
    public void testFileExists() {
        Assertions.assertTrue(fileSystemUtil.getTestImg().exists());
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
                    .header("Authorization", testUser.token())
                    .multiPart("image", fileSystemUtil.getTestImg(), "image/jpeg")
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
    public void userWithoutWriteFileAuthority_UploadsFile_Returns401() {
        for (TestUser user : userUtil.getUsersWithoutAuthority(fileWriteAuth, port)) {
            fileRepository.deleteAll();
            int totalFilesInFileSystemBeforeUpload = fileSystemUtil.getDirectory().list().length;

            RestAssured.given()
                    .header("Authorization", user.token())
                    .multiPart("image", fileSystemUtil.getTestImg(), "image/jpeg")
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
                .header("Authorization", user.token())
                .multiPart("image", fileSystemUtil.getTestTxt(), "application/text")
                .post(url)
                .then()
                .statusCode(400)
                .body("message", equalTo("File is not an image"));

        Assertions.assertEquals(0, fileRepository.findAll().size());
    }

}
