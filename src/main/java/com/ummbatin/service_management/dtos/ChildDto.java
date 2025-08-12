package com.ummbatin.service_management.dtos;

import lombok.Data;
@Data
public class ChildDto {
    private Integer childId;
    private String name;
    private String birthDate;
    private String MotherName ;
    private UserDto user;
    private Integer kindergartenId;
    private String KindergartenName ;
    private Double monthly_fee = 1.1;
// Add this field

    public String getMotherName() {
        return MotherName;
    }
    public void setMonthlyFee(Double monthlyFee) {
        this.monthly_fee = monthly_fee != null ? monthly_fee : 0.0;
    }
    public void setUser(UserDto user) {
        this.user = user;
    }

    public void setKindergartenId(Integer kindergartenId) {
        this.kindergartenId = kindergartenId;
    }


    public void setKindergartenName(String KindergartenName) {
        this.KindergartenName = KindergartenName;
    }
}

