package com.ummbatin.service_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ummbatin.service_management.dtos.ComplaintCreateDTO;
import com.ummbatin.service_management.dtos.ComplaintResponseDTO;
import com.ummbatin.service_management.models.Complaint;
import com.ummbatin.service_management.services.ComplaintService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ComplaintController.class)
class ComplaintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComplaintService complaintService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "RESIDENT")
    void createComplaint_WithoutImage_ShouldWork() throws Exception {
        // 1. إعداد البيانات
        ComplaintCreateDTO dto = new ComplaintCreateDTO();
        dto.setUserId(1L);
        dto.setType("ELECTRICITY");
        dto.setDescription("Test complaint");
        dto.setLocation("Building A");

        Complaint complaint = new Complaint();
        complaint.setComplaintId(1);
        complaint.setUserId(1L);
        complaint.setType("ELECTRICITY");
        complaint.setDescription("Test complaint");
        complaint.setLocation("Building A");

        // 2. Mock الخدمة
        when(complaintService.createComplaint(any(Complaint.class))).thenReturn(complaint);

        // 3. إنشاء multipart بدون صورة
        MockMultipartFile data = new MockMultipartFile(
                "data",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(dto)
        );

        // 4. تنفيذ الاختبار
        mockMvc.perform(multipart("/api/complaints")
                        .file(data)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.complaintId").value(1))
                .andExpect(jsonPath("$.description").value("Test complaint"));
    }
}