package com.ummbatin.service_management.services;

import com.ummbatin.service_management.models.Complaint;
import com.ummbatin.service_management.repositories.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByDateDesc();
    }

    public List<Complaint> getComplaintsByUserId(Long userId) {
        return complaintRepository.findByUserId(userId);
    }

    public Optional<Complaint> getComplaintById(Integer complaintId) {
        return complaintRepository.findById(complaintId);
    }

    public Complaint createComplaint(Complaint complaint) {
        complaint.setDate(LocalDateTime.now());
        if (complaint.getStatus() == null) {
            complaint.setStatus("SUBMITTED");
        }
        return complaintRepository.save(complaint);
    }

    public Complaint updateComplaintStatus(Integer complaintId, String status) {
        return complaintRepository.findById(complaintId).map(existing -> {
            existing.setStatus(status);
            return complaintRepository.save(existing);
        }).orElse(null);
    }

    public void deleteComplaint(Integer complaintId) {
        complaintRepository.deleteById(complaintId);
    }
}