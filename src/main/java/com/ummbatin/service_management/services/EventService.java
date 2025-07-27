package com.ummbatin.service_management.services;

import com.ummbatin.service_management.models.Event;
import com.ummbatin.service_management.repositories.EventRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EventService {

    private final EventRepository repo;
    public EventService(EventRepository repo) { this.repo = repo; }

    public List<Event> getAll() { return repo.findAll(); }
    public Event get(Long id) { return repo.findById(id).orElseThrow(); }
    public Event create(Event e) { return repo.save(e); }
    public Event update(Long id, Event e) {
        Event existing = get(id);
        existing.setTitle(e.getTitle());
        existing.setDescription(e.getDescription());
        existing.setStartDate(e.getStartDate());
        existing.setEndDate(e.getEndDate());
        existing.setLocation(e.getLocation());
        existing.setOrganizer(e.getOrganizer());
        existing.setActive(e.getActive());
        existing.setImageUrl(e.getImageUrl());
        return repo.save(existing);
    }

    public void delete(Long id) { repo.deleteById(id); }
}