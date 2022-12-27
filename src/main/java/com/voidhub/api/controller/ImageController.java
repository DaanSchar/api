package com.voidhub.api.controller;

import com.voidhub.api.service.FileService;
import com.voidhub.api.util.Message;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/api/v1/images")
public class ImageController {

    private @Autowired FileService fileService;

    @PostMapping
    @PreAuthorize("hasAuthority('file:write')")
    public ResponseEntity<Message> uploadImage(@RequestParam("image") MultipartFile image) throws IOException {
        if (!isImage(image)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Message("File is not an image"));
        }

        return fileService.uploadFile(image);
    }

    private boolean isImage(@NonNull MultipartFile file) {
        String type = file.getContentType();
        return type == null || type.equals(MediaType.IMAGE_JPEG_VALUE) || type.equals(MediaType.IMAGE_PNG_VALUE);
    }

}
