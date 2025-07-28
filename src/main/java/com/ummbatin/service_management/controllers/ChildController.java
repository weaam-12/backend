package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.dtos.ChildRequestDTO;
import com.ummbatin.service_management.exceptions.ResourceNotFoundException;
import com.ummbatin.service_management.models.Child;
import com.ummbatin.service_management.models.Kindergarten;
import com.ummbatin.service_management.models.User;
import com.ummbatin.service_management.services.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;

@RestController
@RequestMapping("/api/children")
public class ChildController {
    @Autowired
    private ChildService childService;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Child createChild(@RequestBody ChildRequestDTO childDTO) {
        Child child = new Child();
        child.setName(childDTO.getName());
        child.setBirthDate(childDTO.getBirthDate());

        User user = new User();
        user.setUserId(childDTO.getUserId());
        child.setUser(user);

        if(childDTO.getKindergartenId() != null) {
            Kindergarten kindergarten = new Kindergarten();
            kindergarten.setKindergartenId(childDTO.getKindergartenId());
            child.setKindergarten(kindergarten);
        }

        return childService.createChild(child);
    }

    @Autowired
    public ChildController(ChildService childService) {
        this.childService = childService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Child>> getChildrenByUser(@PathVariable Long userId) {
        List<Child> children = childService.getChildrenByUserId(userId);
        return ResponseEntity.ok(children);
    }

    @GetMapping("/wife/{wifeId}")
    public ResponseEntity<List<Child>> getChildrenByWife(@PathVariable Long wifeId) {
        List<Child> children = childService.getChildrenByWifeId(wifeId);
        return ResponseEntity.ok(children);
    }

    @GetMapping("/my-children")
    public ResponseEntity<List<Child>> getMyChildren() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        List<Child> children = childService.getChildrenByUsername(username);
        return ResponseEntity.ok(children);
    }

    @ControllerAdvice
    public class GlobalExceptionHandler {
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<?> handleGeneralException(Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}