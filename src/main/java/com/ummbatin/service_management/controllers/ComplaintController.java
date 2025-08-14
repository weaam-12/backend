package com.ummbatin.service_management.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ummbatin.service_management.dtos.ComplaintCreateDTO;
import com.ummbatin.service_management.dtos.ComplaintResponseDTO;
import com.ummbatin.service_management.models.Complaint;
import com.ummbatin.service_management.services.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {
    @Autowired
    private ComplaintService complaintService;

    // Resident can view their complaints
    private final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name" ,"dp4a2t3ln",
            "api_key", "621292534649539",
            "api_secret", "X9RvH5Jl5JDXWbmaFC4AlBBrpkI",
            "secure",     true));

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }
    // Resident can create a complaint
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('RESIDENT','ADMIN')")
    public ComplaintResponseDTO createComplaint(
            @RequestPart("data") @Valid ComplaintCreateDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        Complaint c = new Complaint();
        c.setUserId(dto.getUserId());
        c.setType(dto.getType());
        c.setDescription(dto.getDescription());
        c.setLocation(dto.getLocation());

        if (image != null && !image.isEmpty()) {
            var uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("secure_url"); // أو "url" إذا كنتِ لا تستخدمين HTTPS
            c.setImageUrl(imageUrl);
        }

        return ComplaintResponseDTO.fromEntity(complaintService.createComplaint(c));
    }
    @GetMapping("/resident/{userId}")
    @PreAuthorize("hasRole('RESIDENT')")
    public List<Complaint> getMyComplaints(@PathVariable Long userId) {
        return complaintService.getComplaintsByUserId(userId);
    }

    @PatchMapping("/{complaintId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateComplaintStatus(
            @PathVariable Integer complaintId, // غير إلى Integer
            @RequestBody String status) {
        try {
            Complaint updated = complaintService.updateComplaintStatus(complaintId, status);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating complaint status: " + e.getMessage());
        }
    }
}
