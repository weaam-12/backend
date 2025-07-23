package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.models.Event;
import com.ummbatin.service_management.services.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

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