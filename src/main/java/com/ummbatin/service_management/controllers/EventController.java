package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.models.Event;
import com.ummbatin.service_management.services.EventService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService service;
    public EventController(EventService service) { this.service = service; }

    /* 1. عرض جميع الفعاليات */
    @GetMapping
    public List<Event> all() { return service.getAll(); }

    /* 2. عرض فعالية واحدة */
    @GetMapping("/{id}")
    public Event one(@PathVariable Long id) { return service.get(id); }

    /* 3. إضافة فعالية + صورة (حفظ داخل static/uploads) */
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Event create(
            @RequestPart("event") Event event,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        if (image != null && !image.isEmpty()) {
            Path uploadDir = Paths.get("src/main/resources/static/uploads");
            if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

            String fileName = UUID.randomUUID() + "-" + image.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(image.getInputStream(), filePath);

            // المسار الذي سيتم تخزينه في DB وعرضه في <img src="">
            event.setImageUrl("/uploads/" + fileName);
        }
        return service.create(event);
    }

    /* 4. تعديل فعالية */
    @PutMapping("/{id}")
    public Event update(@PathVariable Long id, @RequestBody Event event) {
        return service.update(id, event);
    }

    /* 5. حذف فعالية */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}