package vn.iotstar.catalog.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Upload ảnh cho Category qua REST + multipart (AJAX), tách riêng khỏi GraphQL
 * vì GraphQL mặc định không xử lý file nhị phân. Trả về URL tĩnh để lưu vào
 * trường Category.images.
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/png", "image/jpeg", "image/webp", "image/gif", "image/svg+xml");

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Chưa chọn file để tải lên."));
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Chỉ chấp nhận ảnh PNG, JPG, WEBP, GIF hoặc SVG."));
        }
        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            String original = Optional.ofNullable(file.getOriginalFilename()).orElse("anh");
            String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
            String filename = UUID.randomUUID() + ext;
            file.transferTo(dir.resolve(filename));
            return ResponseEntity.ok(Map.of("url", "/uploads/" + filename));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Không lưu được file: " + e.getMessage()));
        }
    }
}
