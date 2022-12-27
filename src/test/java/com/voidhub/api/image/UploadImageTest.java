package com.voidhub.api.image;

import com.voidhub.api.*;
import com.voidhub.api.configuration.file.FileSystemConfig;
import com.voidhub.api.entity.*;
import com.voidhub.api.repository.*;
import io.restassured.RestAssured;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.*;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UploadImageTest {

    private @Autowired UserRepository userRepository;
    private @Autowired FileRepository fileRepository;
    private @Autowired UserTestUtil userTestUtil;
    private @Autowired FileSystemConfig fileSystemConfig;

    @Value("${local.server.port}")
    private int port;

    private final String fileWriteAuth = "file:write";
    private static File testFile;
    private static String username;
    private static String password;

    @BeforeAll
    public static void beforeAll() {
        username = UUID.randomUUID().toString();
        password = UUID.randomUUID().toString();
        RestAssured.baseURI = "http://localhost";

        testFile = new File("src/test/resources/file-upload/test-files/test-img.jpg");
    }

    @BeforeEach
    public void beforeEach() {
        RestAssured.port = port;
        fileRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testFileExists() {
        Assertions.assertTrue(testFile.exists());
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
    public void userWithFileWriteAuthority_UploadImage_ReturnsCreated() throws IOException {
        for (Role role : Util.getRolesWithAuthority(fileWriteAuth)) {
            fileRepository.deleteAll();
            userRepository.deleteAll();

            var userAndToken = userTestUtil.createUserAndLogin(username, password, role, port);
            int totalFilesInFileSystemBeforeUpload = getFileSystemDirectory().list().length;

            var result = RestAssured.given()
                    .header("Authorization", userAndToken.getSecond())
                    .multiPart("image", testFile, "image/jpeg")
                    .post("api/v1/files")
                    .then()
                    .body("message", equalTo("File uploaded successfully"))
                    .statusCode(201);

            Assertions.assertEquals(1, fileRepository.findAll().size());
            FileData fileData = fileRepository.findAll().get(0);
            result.header("Location", equalTo("/api/v1/files/" + fileData.getId()));

            Assertions.assertEquals(
                    totalFilesInFileSystemBeforeUpload + 1,
                    getFileSystemDirectory().list().length
            );
        }

        clearFileSystem();
    }

    @Test
    public void userWithoutWriteFileAuthority_UploadsFile_Returns401() throws IOException {
        for (Role role : Util.getRolesWithoutAuthority(fileWriteAuth)) {
            fileRepository.deleteAll();
            userRepository.deleteAll();

            var userAndToken = userTestUtil.createUserAndLogin(username, password, role, port);
            int totalFilesInFileSystemBeforeUpload = getFileSystemDirectory().list().length;

            RestAssured.given()
                    .header("Authorization", userAndToken.getSecond())
                    .multiPart("image", testFile, "image/jpeg")
                    .post("api/v1/files")
                    .then()
                    .statusCode(401);

            Assertions.assertEquals(0, fileRepository.findAll().size());
            Assertions.assertEquals(
                    totalFilesInFileSystemBeforeUpload,
                    getFileSystemDirectory().list().length
            );
        }

        clearFileSystem();
    }

    private void clearFileSystem() throws IOException {
        FileUtils.cleanDirectory(new File(fileSystemConfig.getPath()));
    }

    private File getFileSystemDirectory() {
        return new File(fileSystemConfig.getPath());
    }
}
