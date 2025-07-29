package com.ummbatin.service_management.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ummbatin.service_management.dtos.ChildDto;
import jakarta.persistence.*;
import java.time.LocalDate;


    @Entity
    @Table(name = "children")
    public class Child {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "child_id")
        private Integer childId;

        @Column(name = "name", nullable = false)
        private String name;

        @Column(name = "monthly_fee")
        private Double monthlyFee;

        @Column(name = "birth_date", nullable = false)
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthDate;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @ManyToOne
        @JoinColumn(name = "wife_id", nullable = false)
        private Wife wife;


        @ManyToOne
        @JoinColumn(name = "kindergarten_id")
        private Kindergarten kindergarten;

        // Getters and Setters
        public Integer getChildId() {
            return childId;
        }

        public void setChildId(Integer childId) {
            this.childId = childId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LocalDate getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
        }

        public Wife getWife() {
            return wife;
        }

        public void setWife(Wife wife) {
            this.wife = wife;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Kindergarten getKindergarten() {
            return kindergarten;
        }

        public void setKindergarten(Kindergarten kindergarten) {
            this.kindergarten = kindergarten;
        }

        public ChildDto toDto() {
            ChildDto dto = new ChildDto();
            dto.setChildId(this.childId);
            dto.setName(this.name);
            dto.setBirthDate(this.birthDate.toString());
            return dto;
        }
    }
