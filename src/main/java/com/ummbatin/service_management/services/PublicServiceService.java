package com.ummbatin.service_management.services;

import com.ummbatin.service_management.models.PublicService;
import com.ummbatin.service_management.repositories.PublicServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicServiceService {
    @Autowired
    private PublicServiceRepository publicServiceRepository;

    public List<PublicService> getAllServices() {
        return publicServiceRepository.findAll();
    }

    public PublicService createService(PublicService service) {
        return publicServiceRepository.save(service);
    }
}
