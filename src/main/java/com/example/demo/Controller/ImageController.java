package com.example.demo.Controller;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping(value = "images")
public class ImageController {

    @GetMapping(path = "uploads/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException {
        Path path = Paths.get("uploads").resolve(filename).normalize();

        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        byte[] imageBytes = Files.readAllBytes(path);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header("Content-Length", String.valueOf(imageBytes.length))
                .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                .body(imageBytes);
    }

}