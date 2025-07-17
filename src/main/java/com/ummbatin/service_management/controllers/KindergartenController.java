package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.models.Kindergarten;
import com.ummbatin.service_management.repositories.KindergartenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kindergartens")
public class KindergartenController {

    @Autowired
    private KindergartenRepository kindergartenRepository;

    @GetMapping
    public List<Kindergarten> getAllKindergartens() {
        return kindergartenRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kindergarten> getKindergartenById(@PathVariable Integer id) {
        return kindergartenRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Kindergarten createKindergarten(@RequestBody Kindergarten kindergarten) {
        return kindergartenRepository.save(kindergarten);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Kindergarten> updateKindergarten(@PathVariable Integer id, @RequestBody Kindergarten updatedKindergarten) {
        return kindergartenRepository.findById(id).map(kindergarten -> {
            kindergarten.setName(updatedKindergarten.getName());
            kindergarten.setCapacity(updatedKindergarten.getCapacity());
            kindergarten.setLocation(updatedKindergarten.getLocation());
            return ResponseEntity.ok(kindergartenRepository.save(kindergarten));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKindergarten(@PathVariable Integer id) {
        if (kindergartenRepository.existsById(id)) {
            kindergartenRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
