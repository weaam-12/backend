package com.ummbatin.service_management.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ummbatin.service_management.models.Role;
import com.ummbatin.service_management.models.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User data transfer object")
public class UserDto {

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long user_id;

    @Schema(description = "User's full name", example = "Admin User")
    private String fullName;

    @Schema(description = "User's email address", example = "user@example.com")
    private String email;

    @Schema(description = "User's phone number", example = "0501234567")
    private String phone;


    @Schema(description = "User's role", example = "ADMIN", allowableValues = {"USER", "ADMIN"})
    private Role role;

    @Schema(description = "User creation timestamp", example = "2025-06-01T10:57:25")
    private LocalDateTime createdAt;

    public UserDto() {}

    public UserDto(User user) {
        this.user_id = user.getUserId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }

    public UserDto(Long id, String fullName, String email, String phone,  Role role, LocalDateTime createdAt) {
        this.user_id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public Long getUser_id() {
        return user_id;
    }

    public void setId(Long user_id) {
        this.user_id = user_id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Optional Builder pattern

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long user_id;
        private String fullName;
        private String email;
        private String phone;
        private Role role;
        private LocalDateTime createdAt;

        public Builder id(Long user_id) {
            this.user_id = user_id;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }


        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserDto build() {
            return new UserDto(user_id, fullName, email, phone, role, createdAt);
        }
    }
}
