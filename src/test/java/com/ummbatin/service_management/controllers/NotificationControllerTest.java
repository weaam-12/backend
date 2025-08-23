package com.ummbatin.service_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ummbatin.service_management.dtos.NotificationDTO;
import com.ummbatin.service_management.models.Notification;
import com.ummbatin.service_management.models.Role;
import com.ummbatin.service_management.models.User;
import com.ummbatin.service_management.services.NotificationService;
import com.ummbatin.service_management.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = NotificationController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.ummbatin.service_management.config.JwtAuthenticationFilter.class
        ),
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private UserService userService;


    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void adminNotifications_returnsList() throws Exception {
        Notification adminNote = Notification.builder()
                .notificationId(99L)
                .message("System restart")
                .type("ADMIN")
                .status("READ")
                .createdAt(LocalDateTime.now())
                .build();

        when(notificationService.getAdminNotifications()).thenReturn(List.of(adminNote));

        mockMvc.perform(get("/api/notifications/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type", is("ADMIN")));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void adminNotifications_whenEmpty_returnsEmptyArray() throws Exception {
        when(notificationService.getAdminNotifications()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/notifications/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }
}