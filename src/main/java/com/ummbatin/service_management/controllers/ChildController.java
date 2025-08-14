package com.ummbatin.service_management.controllers;

import com.ummbatin.service_management.dtos.ChildDto;
import com.ummbatin.service_management.dtos.ChildRequestDTO;
import com.ummbatin.service_management.exceptions.ResourceNotFoundException;
import com.ummbatin.service_management.models.Child;
import com.ummbatin.service_management.models.Kindergarten;
import com.ummbatin.service_management.models.User;
import com.ummbatin.service_management.repositories.ChildRepository;
import com.ummbatin.service_management.repositories.KindergartenRepository;
import com.ummbatin.service_management.services.ChildService;
import com.ummbatin.service_management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/children")
public class ChildController {
    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private KindergartenRepository kindergartenRepository;

    private final ChildService childService;
    private final UserService userService;

    @Autowired
    public ChildController(ChildService childService, UserService userService) {
        this.childService = childService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Child> createChild(@RequestBody ChildRequestDTO childDTO) {
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

        Child savedChild = childService.createChild(child);
        return ResponseEntity.ok(savedChild);
    }

    @GetMapping("/my-children")
    public ResponseEntity<List<ChildDto>> getMyChildren() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            User user = userService.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            List<Child> children = childService.getChildrenByUserId(user.getUserId());

            List<ChildDto> childDtos = children.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(childDtos);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ChildDto convertToDto(Child child) {
        ChildDto dto = new ChildDto();
        dto.setChildId(child.getChildId());
        dto.setName(child.getName());
        dto.setBirthDate(child.getBirthDate().toString());
        dto.setMonthlyFee(child.getMonthly_fee());          // ← أضفه
        if (child.getKindergarten() != null) {
            dto.setKindergartenId(child.getKindergarten().getKindergartenId());
            dto.setKindergartenName(child.getKindergarten().getName());
        }
        if (child.getWife() != null) {
            dto.setMotherName(child.getWife().getName());   // ← أضفه
        }
        return dto;
    }

    @PatchMapping("/{childId}/approve")
    public ResponseEntity<Object> approveChild(
            @PathVariable Integer childId,
            @RequestParam Boolean approved) {
        return childRepository.findById(childId).map(child -> {
            child.setMonthly_fee(approved ? 3.5 : 1.5);
            childRepository.save(child);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Child>> getChildrenByUser(@PathVariable Long userId) {
        List<Child> children = childService.getChildrenByUserId(userId);
        return ResponseEntity.ok(children);
    }

    @PatchMapping("/{childId}/assign")
    public ResponseEntity<Object> assignChildToKindergarten(
            @PathVariable Integer childId,
            @RequestParam Integer kindergartenId,
            @RequestParam Double monthlyFee) {

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Child not found"));

        Kindergarten kg = kindergartenRepository.findById(kindergartenId)
                .orElseThrow(() -> new ResourceNotFoundException("Kindergarten not found"));

        child.setKindergarten(kg);
        child.setMonthly_fee(monthlyFee);
        childRepository.save(child);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/wife/{wifeId}")
    public ResponseEntity<List<Child>> getChildrenByWife(@PathVariable Long wifeId) {
        List<Child> children = childService.getChildrenByWifeId(wifeId);
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