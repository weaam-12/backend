package com.ummbatin.service_management.services;

import com.ummbatin.service_management.dtos.FamilyRegistrationDto;
import com.ummbatin.service_management.models.*;
import com.ummbatin.service_management.repositories.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
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
        // 1. إنشاء المستخدم
        User user = new User();
        user.setFullName(dto.getUser().getFullName());
        user.setEmail(dto.getUser().getEmail());
        user.setPassword(passwordEncoder.encode(dto.getUser().getPassword()));
        user.setPhone(dto.getUser().getPhone());
        user.setUserId(dto.getUser().getUserId());
        Role userRole = roleRepository.findByRoleName("RESIDENT")
                .orElseThrow(() -> new RuntimeException("Role RESIDENT not found"));
        user.setRole(userRole);

        User savedUser = userRepository.save(user);
        log.info("User saved with ID: {}", savedUser.getUserId());

        // 2. حفظ الزوجات
        List<Wife> wives = dto.getWives().stream()
                .filter(wifeDto -> wifeDto.getName() != null && !wifeDto.getName().isBlank())
                .map(wifeDto -> {
                    Wife wife = new Wife();
                    wife.setName(wifeDto.getName());
                    wife.setUser(savedUser);
                    return wife;
                }).toList();

        List<Wife> savedWives = wifeRepository.saveAll(wives);
        log.info("Wives saved: {}", savedWives.size());

        // 3. حفظ الأبناء
        List<Child> children = dto.getChildren().stream()
                .filter(childDto -> childDto.getName() != null && !childDto.getName().isBlank())
                .map(childDto -> {
                    Child child = new Child();
                    child.setName(childDto.getName());
                    child.setBirthDate(LocalDate.parse(childDto.getBirthDate()));
                    child.setUser(savedUser);

                    if (childDto.getWifeIndex() >= 0 && childDto.getWifeIndex() < savedWives.size()) {
                        child.setWife(savedWives.get(childDto.getWifeIndex()));
                    } else {
                        throw new RuntimeException("Invalid wife index");
                    }
                    return child;
                }).toList();

        childRepository.saveAll(children);
        log.info("Children saved: {}", children.size());

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

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
}