package com.ummbatin.service_management.services;

import com.ummbatin.service_management.dtos.ChildDto;
import com.ummbatin.service_management.dtos.KindergartenDto;
import com.ummbatin.service_management.dtos.UserDto;
import com.ummbatin.service_management.models.Child;
import com.ummbatin.service_management.models.Kindergarten;
import com.ummbatin.service_management.models.User;
import com.ummbatin.service_management.repositories.KindergartenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KindergartenService {

    private final KindergartenRepository kindergartenRepository;

    @Autowired
    public KindergartenService(KindergartenRepository kindergartenRepository) {
        this.kindergartenRepository = kindergartenRepository;
    }

    public List<KindergartenDto> getAllKindergartens() {
        return kindergartenRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private KindergartenDto convertToDto(Kindergarten kindergarten) {
        KindergartenDto dto = new KindergartenDto();
        dto.setKindergartenId(kindergarten.getKindergartenId());
        dto.setName(kindergarten.getName());
        dto.setCapacity(kindergarten.getCapacity());
        dto.setLocation(kindergarten.getLocation());
        dto.setChildren(kindergarten.getChildren().stream()
                .map(this::convertChildToDto)
                .collect(Collectors.toList()));
        return dto;
    }

    private ChildDto convertChildToDto(Child child) {
        ChildDto dto = new ChildDto();
        dto.setChildId(child.getChildId());
        dto.setName(child.getName());
        dto.setBirthDate(child.getBirthDate().toString());
        dto.setUser(convertUserToDto(child.getUser()));
        return dto;
    }

    private UserDto convertUserToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        return dto;
    }
}