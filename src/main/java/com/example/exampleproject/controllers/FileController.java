package com.example.exampleproject.controllers;

import com.example.exampleproject.configs.annotations.MultipartFileValidation;
import com.example.exampleproject.dto.request.AdditionalDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/files")
public class FileController {

    @PostMapping(value = "/upload-details", consumes = "multipart/form-data")
    public String uploadFileWithDetails(
            @MultipartFileValidation(allowedTypes = {"application/pdf"}, maxSizeInMB = 5)
            @RequestParam("file")
            MultipartFile file,

            @RequestParam("userId")
            String userId,

            @RequestPart("additionalDetails")
            AdditionalDetails additionalDetails) {

        String fileName = file.getOriginalFilename();
        long fileSize = file.getSize();
        String contentType = file.getContentType();

        return String.format(
                "'%s' file received (size: %d bytes). Content %s. UserId parameter: %s, additional details: %s",
                fileName,
                fileSize,
                contentType,
                userId,
                additionalDetails.toString()
        );
    }

    @PostMapping(value = "/upload-multiple-list", consumes = "multipart/form-data")
    public String uploadMultipleFilesList(
            @MultipartFileValidation(
                allowedTypes = {"image/jpeg", "image/png", "application/pdf"}, 
                maxSizeInMB = 5,
                maxFileCount = 3
            )
            @RequestParam("files") 
            List<MultipartFile> files) {

        return String.format(
                "Received %d files: %s",
                files.size(),
                files.stream()
                        .map(file -> file.getOriginalFilename() + " (" + file.getSize() + " bytes)")
                        .collect(Collectors.joining(", "))
        );
    }
}
