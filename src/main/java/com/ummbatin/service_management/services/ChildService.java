package com.ummbatin.service_management.services;

import com.ummbatin.service_management.models.Child;
import com.ummbatin.service_management.repositories.ChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChildService {
    @Autowired
    private ChildRepository childRepository;

    public List<Child> getChildrenByUserId(Long userId) {
        return childRepository.findByUserId(userId);
    }

    public List<Child> getChildrenByWifeId(Long wifeId) {
        return childRepository.findByWifeId(wifeId);
    }

    public Child createChild(Child child) {
        return childRepository.save(child);
    }
}