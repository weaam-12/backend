package com.ummbatin.service_management.models;

import com.ummbatin.service_management.dtos.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String fullName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Property> properties;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    private String phone;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    // Automatically set createdAt before persisting to DB
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(role.getRoleName().toUpperCase())
        );
    }
    @Column(name = "last_water_reading")
    private Double lastWaterReading;

    // مع الجيتار والستار
    public Double getLastWaterReading() {
        return lastWaterReading;
    }

    public void setLastWaterReading(Double lastWaterReading) {
        this.lastWaterReading = lastWaterReading;
    }
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Child> children;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Wife> wives;

    // مع الجيتار والستار
    public List<Wife> getWives() {
        return wives;
    }

    public void setWives(List<Wife> wives) {
        this.wives = wives;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }
    public UserDto toDto() {
        return UserDto.builder()
                .id(this.userId)
                .fullName(this.fullName)
                .email(this.email)
                .phone(this.phone)
                .role(this.role)
                .createdAt(this.createdAt)
                .properties(this.properties != null ?
                        this.properties.stream()
                                .map(Property::toDto)
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .wives(this.wives != null ?
                        this.wives.stream()
                                .map(Wife::toDto)
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .children(this.children != null ?
                        this.children.stream()
                                .map(Child::toDto)
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .build();
    }
}
