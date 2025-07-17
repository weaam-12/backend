package com.ummbatin.service_management.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "water_readings")
public class WaterReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "approved", nullable = false)
    private Boolean approved;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "is_manual", nullable = false)
    private Boolean isManual;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }
}
