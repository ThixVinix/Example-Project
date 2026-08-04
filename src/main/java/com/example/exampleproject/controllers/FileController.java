package com.example.exampleproject.controllers;

import io.github.thixvinix.commons.files.spring.MultipartFileValidation;
import com.example.exampleproject.dto.request.AdditionalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Files", description = "Endpoints for uploading and validating files")
@Validated
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Operation(
            operationId = "uploadFileWithDetails",
            summary = "Uploads a single PDF with extra details",
            description = "Receives a single PDF file along with the user id and a JSON part with additional details."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Upload processed successfully",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            )
    })
    @PostMapping(value = "/upload-details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFileWithDetails(
            @Parameter(
                    description = "PDF file to upload. Max size: 5 MB. Allowed types: application/pdf",
                    required = true,
                    content = @Content(mediaType = "application/pdf",
                            schema = @Schema(type = "string", format = "binary"))
            )
            @MultipartFileValidation(allowedTypes = {"application/pdf"}, maxSizeInMB = 5)
            @RequestParam("file")
            MultipartFile file,

            @Parameter(description = "User identifier associated with the upload",
                    example = "user-123", required = true)
            @RequestParam("userId")
            String userId,

            @Parameter(
                    description = "Additional JSON details related to the upload",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdditionalDetails.class))
            )
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

    @Operation(
            operationId = "uploadMultipleFilesList",
            summary = "Uploads up to 3 files as a list",
            description = "Receives a list with up to 3 files (JPEG, PNG or PDF). Each file can be up to 5 MB."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Upload processed successfully",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class))
            )
    })
    @PostMapping(value = "/upload-multiple-list", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadMultipleFilesList(
            @Parameter(
                    description = "Files to upload. Max 3 files. Allowed types: image/jpeg, image/png, " +
                            "application/pdf. Max size per file: 5 MB",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            array = @ArraySchema(schema = @Schema(type = "string", format = "binary")))
            )
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
