package com.hopital.urgences.controller;

import com.hopital.urgences.dto.RegisterUserRequest;
import com.hopital.urgences.model.User;
import com.hopital.urgences.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> creer(@RequestBody @Valid RegisterUserRequest requete) {
        User user = User.builder()
                .username(requete.username())
                .passwordHash(passwordEncoder.encode(requete.password()))
                .role(requete.role())
                .nom(requete.nom())
                .prenom(requete.prenom())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(user));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> lister() {
        return userRepository.findAll();
    }
}