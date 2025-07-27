package com.ummbatin.service_management.models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "kindergartens")
public class Kindergarten {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kindergarten_id")
    private Integer kindergartenId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "location", nullable = false)
    private String location;


    @Transient
    public Integer getChildrenCount() {
        return children != null ? children.size() : 0;
    }

    @Transient
    public Integer getPendingRequests() {
        return enrollments != null ?
                (int) enrollments.stream().filter(e -> "PENDING".equals(e.getStatus())).count() : 0;
    }
    @OneToMany(mappedBy = "kindergarten", cascade = CascadeType.ALL)
    private Set<Child> children = new HashSet<>();

    @OneToMany(mappedBy = "kindergarten", cascade = CascadeType.ALL)
    private Set<Enrollment> enrollments = new HashSet<>();

    // Getters and Setters
    public Integer getKindergartenId() {
        return kindergartenId;
    }

    public void setKindergartenId(Integer kindergartenId) {
        this.kindergartenId = kindergartenId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<Child> getChildren() {
        return children;
    }

    public void setChildren(Set<Child> children) {
        this.children = children;
    }

    public Set<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(Set<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }
}
