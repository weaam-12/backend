package com.ummbatin.service_management.models;

import jakarta.persistence.*;
import java.util.List;

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

    @OneToMany(mappedBy = "kindergarten", cascade = CascadeType.ALL)
    private List<Child> children;

    @OneToMany(mappedBy = "kindergarten", cascade = CascadeType.ALL)
    private List<Enrollment> enrollments;

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

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }
}
