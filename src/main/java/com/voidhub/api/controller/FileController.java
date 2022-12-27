package com.voidhub.api.controller;

import com.voidhub.api.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/files")
public class FileController {

    private @Autowired FileService fileService;

    @GetMapping("/{file_id}")
    public ResponseEntity<?> getImage(@PathVariable(name = "file_id") UUID id) throws IOException {
        return fileService.getFile(id);
    }

}
