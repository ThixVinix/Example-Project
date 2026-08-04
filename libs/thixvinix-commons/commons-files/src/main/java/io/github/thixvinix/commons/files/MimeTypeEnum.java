package io.github.thixvinix.commons.files;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Maps common file extensions to their MIME types, and provides reverse lookups (MIME type
 * to extension, extension validity) used to enforce correct MIME type / extension pairing.
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
     * @return the file extension associated with the given MIME type, or null if not found
     */
    public static String getExtensionFromMimeType(String mimeType) {
        return Arrays.stream(MimeTypeEnum.values())
                .filter(type -> type.getMimeType().equals(mimeType))
                .findFirst()
                .map(MimeTypeEnum::getExtension)
                .orElse(null);
    }

    /**
     * Determines if the provided file extension is invalid by checking against a predefined list of valid extensions.
     *
     * @return true if the extension is invalid or not found in the predefined list, false otherwise.
     */
    public static boolean isNotValidExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return true;
        }

        return Arrays.stream(MimeTypeEnum.values())
                .map(MimeTypeEnum::getExtension)
                .noneMatch(ext -> ext.equalsIgnoreCase(extension));
    }

}
