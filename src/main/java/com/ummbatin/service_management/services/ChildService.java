package com.ummbatin.service_management.services;

import com.ummbatin.service_management.exceptions.ResourceNotFoundException;
import com.ummbatin.service_management.models.Child;
import com.ummbatin.service_management.models.User;
import com.ummbatin.service_management.repositories.ChildRepository;
import com.ummbatin.service_management.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChildService {
    @Autowired
    private ChildRepository childRepository;

    UserRepository userRepository;


    public Child createChild(Child child) {
        return childRepository.save(child);
    }
    public List<Child> getChildrenByUserId(Long userId) {
        return childRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No children found for user id: " + userId));
    }

    // البحث بأيدي الزوجة
    public List<Child> getChildrenByWifeId(Long wifeId) {
        return childRepository.findByWifeId(wifeId)
                .orElseThrow(() -> new ResourceNotFoundException("No children found for wife id: " + wifeId));
    }

    // البحث باسم المستخدم
    public List<Child> getChildrenByUsername(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));

        return getChildrenByUserId(user.getUserId());
    }
}