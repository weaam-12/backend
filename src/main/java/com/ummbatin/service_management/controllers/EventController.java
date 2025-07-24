package com.ummbatin.service_management.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ummbatin.service_management.models.Event;
import com.ummbatin.service_management.services.EventService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    // 1) تهيئة Cloudinary مرة واحدة
    private final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name" ,"dp4a2t3ln",
            "api_key", "621292534649539",
            "api_secret", "X9RvH5Jl5JDXWbmaFC4AlBBrpkI",
            "secure",     true));

    private final EventService service;
    public EventController(EventService service) { this.service = service; }

    /* ---------- REST endpoints ---------- */

    @GetMapping
    public List<Event> all() { return service.getAll(); }

    @GetMapping("/{id}")
    public Event one(@PathVariable Long id) { return service.get(id); }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Event create(
            @RequestPart("event") Event event,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        if (image != null && !image.isEmpty()) {
            // 2) رفع الصورة إلى Cloudinary
            Map uploadResult = cloudinary.uploader()
                    .upload(image.getBytes(), ObjectUtils.emptyMap());

            // 3) استخراج الرابط الآمن المباشر
            String imageUrl = (String) uploadResult.get("secure_url");
            event.setImageUrl(imageUrl);
        }
        return service.create(event);
    }

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