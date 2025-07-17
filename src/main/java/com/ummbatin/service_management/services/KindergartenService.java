package com.ummbatin.service_management.services;

import com.ummbatin.service_management.models.Kindergarten;
import com.ummbatin.service_management.repositories.KindergartenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KindergartenService {

    private final KindergartenRepository kindergartenRepository;

    @Autowired
    public KindergartenService(KindergartenRepository kindergartenRepository) {
        this.kindergartenRepository = kindergartenRepository;
    }

    public List<Kindergarten> getAllKindergartens() {
        return kindergartenRepository.findAll();
    }

    public Optional<Kindergarten> getKindergartenById(Integer id) {
        return kindergartenRepository.findById(id);
    }

    public Kindergarten createKindergarten(Kindergarten kindergarten) {
        return kindergartenRepository.save(kindergarten);
    }

    public Kindergarten updateKindergarten(Integer id, Kindergarten updatedKindergarten) {
        return kindergartenRepository.findById(id).map(existing -> {
            existing.setName(updatedKindergarten.getName());
            existing.setCapacity(updatedKindergarten.getCapacity());
            existing.setLocation(updatedKindergarten.getLocation());
            return kindergartenRepository.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("Kindergarten not found with ID: " + id));
    }

    public void deleteKindergarten(Integer id) {
        kindergartenRepository.deleteById(id);
    }
}
