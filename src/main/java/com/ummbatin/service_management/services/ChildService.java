package com.ummbatin.service_management.services;

import com.ummbatin.service_management.exceptions.ResourceNotFoundException;
import com.ummbatin.service_management.models.Child;
import com.ummbatin.service_management.models.User;
import com.ummbatin.service_management.repositories.ChildRepository;
import com.ummbatin.service_management.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChildService {
    @Autowired
    private ChildRepository childRepository;

    UserRepository userRepository;

    @Autowired
    public ChildService(ChildRepository childRepository) {
        this.childRepository = childRepository;
    }

    public List<Child> getChildrenByUserId(Long userId) {
        return childRepository.findAllByUserId(userId);   // أو الـ Query المخصّصة
    }
    public List<Child> getChildrenByWifeId(Long wifeId) {
        return childRepository.findByWifeId(wifeId);
    }

    public Child createChild(Child child) {
        return childRepository.save(child);
    }
    

    public Optional<Child> getChildById(Long childId) {
        return childRepository.findById(Math.toIntExact(childId));
    }



}