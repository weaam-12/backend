package com.ummbatin.service_management.models;

import com.ummbatin.service_management.dtos.WifeDto;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "wives")
public class Wife {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "wife", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Child> children;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User use;
    public WifeDto toDto() {
        WifeDto dto = new WifeDto();
        dto.setId(this.id);
        dto.setName(this.name);
        return dto;
    }
    // Getters and Setters
}