package com.ummbatin.service_management.services;

import com.ummbatin.service_management.dtos.FamilyRegistrationDto;
import com.ummbatin.service_management.models.Child;
import com.ummbatin.service_management.models.Role;
import com.ummbatin.service_management.models.User;
import com.ummbatin.service_management.models.Wife;
import com.ummbatin.service_management.repositories.ChildRepository;
import com.ummbatin.service_management.repositories.RoleRepository;
import com.ummbatin.service_management.repositories.UserRepository;
import com.ummbatin.service_management.repositories.WifeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<User> getAllUsers() {
        return userRepository.findAllWithProperties();
    }
    public User registerUser(String email, String password, String roleName) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User already exists");
        }

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        return userRepository.save(user);
    }

    @Transactional
    public User registerFamily(FamilyRegistrationDto dto) {
        // 1. create user
        User user = dto.getUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(
                roleRepository.findByRoleName("USER")
                        .orElseThrow(() -> new RuntimeException("Role USER not found"))
        );
        user = userRepository.save(user);

        // 2. wives
        User finalUser = user;
        List<Wife> wivesEntities = dto.getWives().stream()
                .filter(name -> name != null && !name.isBlank())
                .map(name -> {
                    Wife w = new Wife();
                    w.setName(name);
                    w.setUser(finalUser);
                    return w;
                }).toList();
        wifeRepository.saveAll(wivesEntities);

        // 3. children
        List<Wife> finalWives = wivesEntities;

        List<Child> childEntities = dto.getChildren().stream()
                .filter(c -> c.getName() != null && !c.getName().isBlank())
                .map(c -> {
                    Child child = new Child();
                    child.setName(c.getName());
                    child.setBirthDate(LocalDate.parse(c.getBirthDate()));
                    child.setUserId(finalUser.getUserId());
                    Wife mother = finalWives.get(c.getWifeIndex());
                    child.setWifeId(mother.getId());
                    return child;
                }).toList();
        childRepository.saveAll(childEntities);

        return user;
    }
}
