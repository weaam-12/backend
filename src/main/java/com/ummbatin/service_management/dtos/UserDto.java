package com.ummbatin.service_management.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ummbatin.service_management.models.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "User data transfer object")
public class UserDto {

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;

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

    @Schema(description = "List of user properties")
    private List<PropertyDto> properties;

    @Schema(description = "List of user's wives")
    private List<WifeDto> wives;

    @Schema(description = "List of user's children")
    private List<ChildDto> children;

    public static UserDtoBuilder builder() {
        return new UserDtoBuilder();
    }

    public static class UserDtoBuilder {
        private Long id;
        private String fullName;
        private String email;
        private String phone;
        private Role role;
        private LocalDateTime createdAt;
        private List<PropertyDto> properties = Collections.emptyList();
        private List<WifeDto> wives = Collections.emptyList();
        private List<ChildDto> children = Collections.emptyList();

        public UserDtoBuilder user_id(Long user_id) {
            this.id = user_id;
            return this;
        }

        public UserDtoBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserDtoBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserDtoBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UserDtoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserDtoBuilder properties(List<PropertyDto> properties) {
            this.properties = properties;
            return this;
        }

        public UserDtoBuilder wives(List<WifeDto> wives) {
            this.wives = wives;
            return this;
        }

        public UserDtoBuilder children(List<ChildDto> children) {
            this.children = children;
            return this;
        }

        public UserDto build() {
            UserDto userDto = new UserDto();
            userDto.setUser_id(this.id);
            userDto.setFullName(this.fullName);
            userDto.setEmail(this.email);
            userDto.setPhone(this.phone);
            userDto.setRole(this.role);
            userDto.setCreatedAt(this.createdAt);
            userDto.setProperties(this.properties);
            userDto.setWives(this.wives);
            userDto.setChildren(this.children);
            return userDto;
        }
    }

    public UserDto(User user) {
        this.id = user.getUserId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();

        this.properties = user.getProperties() != null ?
                user.getProperties().stream()
                        .map(Property::toDto)
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        this.wives = user.getWives() != null ?
                user.getWives().stream()
                        .map(Wife::toDto)
                        .collect(Collectors.toList()) :
                Collections.emptyList();

        this.children = user.getChildren() != null ?
                user.getChildren().stream()
                        .map(Child::toDto)
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }

    private void setUser_id(Long id) {
        this.id = id;
    }
}