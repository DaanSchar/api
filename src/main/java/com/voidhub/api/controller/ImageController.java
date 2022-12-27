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

import java.util.UUID;

@Controller
@RequestMapping("/api/v1/files")
public class ImageController {

    @Autowired
    private FileService fileService;

    @PostMapping
    @PreAuthorize("hasAuthority('file:write')")
    public ResponseEntity<Message> uploadImage(@RequestParam("image") MultipartFile image) {
        if (!isImage(image)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Message("File is not an image"));
        }

        return fileService.uploadFile(image);
    }

    @GetMapping("/{file_id}")
    public ResponseEntity<?> getImage(@PathVariable(name = "file_id") UUID id) {
        return fileService.getFile(id);
    }

    private boolean isImage(@NonNull MultipartFile file) {
        String type = file.getContentType();

        if (type == null) {
            return false;
        }

        return type.equals(MediaType.IMAGE_JPEG_VALUE) || type.equals(MediaType.IMAGE_PNG_VALUE);
    }

}
