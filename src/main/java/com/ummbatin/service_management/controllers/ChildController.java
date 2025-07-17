package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.dtos.ChildRequestDTO;
import com.ummbatin.service_management.models.Child;
import com.ummbatin.service_management.models.Kindergarten;
import com.ummbatin.service_management.services.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/children")
public class ChildController {
    @Autowired
    private ChildService childService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<Child> getChildrenByUser(@PathVariable Long userId) {
        return childService.getChildrenByUserId(userId);
    }

    @GetMapping("/wife/{wifeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<Child> getChildrenByWife(@PathVariable Long wifeId) {
        return childService.getChildrenByWifeId(wifeId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Child createChild(@RequestBody ChildRequestDTO childDTO) {
        Child child = new Child();
        child.setName(childDTO.getName());
        child.setBirthDate(childDTO.getBirthDate());
        child.setWifeId(childDTO.getWifeId());
        child.setUserId(childDTO.getUserId());

        if(childDTO.getKindergartenId() != null) {
            Kindergarten kindergarten = new Kindergarten();
            kindergarten.setKindergartenId(childDTO.getKindergartenId());
            child.setKindergarten(kindergarten);
        }

        return childService.createChild(child);
    }
}