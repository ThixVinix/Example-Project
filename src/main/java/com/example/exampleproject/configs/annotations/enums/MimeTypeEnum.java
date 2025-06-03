package com.example.exampleproject.configs.annotations.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Enum representing various MIME types associated with specific file extensions.
 * This enumeration maps file extensions to their corresponding MIME types,
 * providing a standardized way to identify content types.
 * <p>
 * The enum provides utility methods for reverse lookups,
 * such as retrieving a file extension based on a given MIME type.
 * <p>
 * Usage of this class can help enforce correct MIME type and extension associations
 * across different areas of an application.
 */
@Getter
@RequiredArgsConstructor
public enum MimeTypeEnum {

    // Images
    JPEG("image/jpeg", "jpg"),
    PNG("image/png", "png"),
    GIF("image/gif", "gif"),
    BMP("image/bmp", "bmp"),
    WEBP("image/webp", "webp"),
    TIFF("image/tiff", "tiff"),
    ICON("image/x-icon", "ico"),

    // Texts
    PLAIN("text/plain", "txt"),
    CSV("text/csv", "csv"),
    JSON("application/json", "json"),
    YAML("application/x-yaml", "yaml"),
    HTML("text/html", "html"),
    CSS("text/css", "css"),
    XML("application/xml", "xml"),
    MARKDOWN("text/markdown", "md"),

    // Compression files
    ZIP("application/zip", "zip"),
    SEVEN_ZIP("application/x-7z-compressed", "7z"),
    RAR("application/x-rar-compressed", "rar"),
    TAR("application/x-tar", "tar"),
    GZIP("application/gzip", "gz"),
    BZIP2("application/x-bzip2", "bz2"),
    LZMA("application/x-lzma", "lzma"),
    ZSTD("application/zstd", "zst"),

    // Audios
    MP3("audio/mpeg", "mp3"),
    WAV("audio/wav", "wav"),
    AAC("audio/aac", "aac"),
    OGG("audio/ogg", "ogg"),
    FLAC("audio/flac", "flac"),
    AIFF("audio/aiff", "aiff"),
    MID("audio/midi", "mid"),

    // Videos
    MP4("video/mp4", "mp4"),
    AVI("video/x-msvideo", "avi"),
    WMV("video/x-ms-wmv", "wmv"),
    WEBM("video/webm", "webm"),
    MOV("video/quicktime", "mov"),
    MKV("video/x-matroska", "mkv"),
    FLV("video/x-flv", "flv"),
    MPEG("video/mpeg", "mpeg"),

    // Documents
    PDF("application/pdf", "pdf"),
    DOC("application/msword", "doc"),
    PPT("application/vnd.ms-powerpoint", "ppt"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx"),

    // Script Files
    JAVASCRIPT("application/javascript", "js"),
    PYTHON("text/x-python", "py"),
    JAVA("text/x-java-source", "java"),
    KOTLIN("text/x-kotlin", "kt"),
    SCALA("text/x-scala", "scala"),
    TYPESCRIPT("application/x-typescript", "ts"),
    PHP("application/x-httpd-php", "php"),
    RUBY("text/x-ruby", "rb"),
    C("text/x-c", "c"),
    CPP("text/x-c++", "cpp"),
    CSHARP("text/x-csharp", "cs"),
    GO("text/x-go", "go"),
    RUST("text/x-rust", "rs"),
    SWIFT("text/x-swift", "swift"),
    PERL("text/x-perl", "pl"),
    LUA("text/x-lua", "lua");

    private final String mimeType;
    private final String extension;

    /**
     * Retrieves the file extension corresponding to the provided MIME type.
     *
     * @param mimeType the MIME type for which the file extension is to be determined; can be null or invalid.
     * @return the file extension associated with the given MIME type, or null if the MIME type is invalid
     *         or not found in the predefined set of MIME types.
     */
    public static String getExtensionFromMimeType(String mimeType) {
        return Arrays.stream(MimeTypeEnum.values())
                .filter(type -> type.getMimeType().equals(mimeType))
                .findFirst()
                .map(MimeTypeEnum::getExtension)
                .orElse(null);
    }


}
