package com.voidhub.api.service;

import com.voidhub.api.configuration.file.FileSystemConfig;
import com.voidhub.api.entity.FileData;
import com.voidhub.api.repository.FileRepository;
import com.voidhub.api.util.Message;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Service
public class FileService {

    private @Autowired FileRepository fileRepository;
    private @Autowired FileSystemConfig fileSystemConfig;

    public ResponseEntity<Message> uploadFile(MultipartFile file) {
        String filePath = fileSystemConfig.getPath() + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

        FileData fileData = fileRepository.save(FileData.builder()
                .name(file.getName())
                .type(file.getContentType())
                .filePath(filePath)
                .build());

        try {
            file.transferTo(new java.io.File(filePath));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new Message("Error while uploading file"));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/api/v1/files/" + fileData.getId())
                .body(new Message("File uploaded successfully"));
    }

    public ResponseEntity<?> getFile(UUID id) {
        FileData fileData = fileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("File does not exist"));

        String filePath = fileData.getFilePath();
        byte[] file;

        try {
            file = Files.readAllBytes(new java.io.File(filePath).toPath());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(new Message("Error while reading file"));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(fileData.getType()))
                .body(file);
    }

}
