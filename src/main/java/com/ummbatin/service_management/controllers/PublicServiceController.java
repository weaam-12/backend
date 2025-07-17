package com.ummbatin.service_management.controllers;
import com.ummbatin.service_management.models.PublicService;
import com.ummbatin.service_management.services.PublicServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public-services")
public class PublicServiceController {
    @Autowired
    private PublicServiceService publicServiceService;

    // Anyone can view all services
    @GetMapping
    public List<PublicService> getAllServices() {
        return publicServiceService.getAllServices();
    }

    // Only Admin can add a service
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PublicService createService(@RequestBody PublicService service) {
        return publicServiceService.createService(service);
    }
}
