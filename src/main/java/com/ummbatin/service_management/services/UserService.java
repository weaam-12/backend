package com.ummbatin.service_management.services;

import com.ummbatin.service_management.dtos.FamilyRegistrationDto;
import com.ummbatin.service_management.models.*;
import com.ummbatin.service_management.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WifeRepository wifeRepository;
    @Autowired
    private ChildRepository childRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    public User registerFamily(FamilyRegistrationDto dto) {
        // 1. Create user
        User user = dto.getUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found")));

        User savedUser = userRepository.save(user);

        // 2. Save wives
        List<Wife> wives = dto.getWives().stream()
                .filter(name -> name != null && !name.isBlank())
                .map(name -> {
                    Wife wife = new Wife();
                    wife.setName(name);
                    wife.setUser(savedUser);
                    return wife;
                }).toList();

        List<Wife> savedWives = wifeRepository.saveAll(wives);

        // 3. Save children
        List<Child> children = dto.getChildren().stream()
                .filter(c -> c.getName() != null && !c.getName().isBlank())
                .map(c -> {
                    Child child = new Child();
                    child.setName(c.getName());
                    child.setBirthDate(LocalDate.parse(c.getBirthDate()));
                    child.setUser(savedUser);                             // ✅
                    child.setWife(savedWives.get(c.getWifeIndex()));
                    return child;
                }).toList();

        childRepository.saveAll(children);

        return savedUser;
    }

    @Transactional
    public User updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRole(role);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}