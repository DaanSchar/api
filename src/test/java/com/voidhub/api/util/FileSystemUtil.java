package com.voidhub.api.util;


import com.voidhub.api.configuration.file.FileSystemConfig;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class FileSystemUtil {

    private @Autowired FileSystemConfig fileSystemConfig;

    public void clear() throws IOException {
        FileUtils.cleanDirectory(getDirectory());
    }

    public File getDirectory() {
        return new File(fileSystemConfig.getPath());
    }

    public File getTestImg() {
        return new File("src/test/resources/file-upload/test-files/test-img.jpg");
    }

    public File getTestTxt() {
        return new File("src/test/resources/file-upload/test-files/test-txt.txt");
    }

}
