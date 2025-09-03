package com.ummbatin.service_management.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "water_readings")
public class WaterReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(name = "amount", nullable = false)
    private Double amount;


    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "reading", nullable = true)
    private LocalDateTime reading;

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


    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }


    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public void setReading(Double reading) {
    }
}
