package com.ummbatin.service_management.dtos;

import com.ummbatin.service_management.models.Complaint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintResponseDTO {
    private Integer complaintId;
    private Long userId;
    private String description;
    private String status;
    private LocalDateTime date;
    private String response;
    private String location;
    private String type;
    private String ticketNumber;
    private String imageUrl;

    public static ComplaintResponseDTO fromEntity(Complaint complaint) {
        return new ComplaintResponseDTO(
                complaint.getComplaintId(),
                complaint.getUserId(),
                complaint.getDescription(),
                complaint.getStatus(),
                complaint.getDate(),
                complaint.getResponse(),
                complaint.getLocation(),
                complaint.getType(),
                complaint.getTicketNumber(),
                complaint.getImageUrl()
        );
    }
}