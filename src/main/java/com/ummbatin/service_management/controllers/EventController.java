package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.models.Event;
import com.ummbatin.service_management.services.EventService;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @SneakyThrows
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Event create(
            @RequestPart("event") Event event,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            // حفظ الصورة (مثلاً على S3 أو ديسك)
            String fileName = UUID.randomUUID() + "-" + image.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            Files.copy(image.getInputStream(), path);
            event.setImageUrl(path.toString());
        }
        return service.create(event);
    }
    private final EventService service;
    public EventController(EventService service) { this.service = service; }

    @GetMapping
    public List<Event> all() { return service.getAll(); }

    @GetMapping("/{id}")
    public Event one(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    public Event create(@RequestBody Event event) { return service.create(event); }

    @PutMapping("/{id}")
    public Event update(@PathVariable Long id, @RequestBody Event event) {
        return service.update(id, event);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}