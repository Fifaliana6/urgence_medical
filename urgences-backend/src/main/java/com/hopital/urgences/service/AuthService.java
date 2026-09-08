package com.hopital.urgences.service;

import com.hopital.urgences.dto.login.LoginRequest;
import com.hopital.urgences.dto.login.LoginResponse;
import com.hopital.urgences.dto.login.RegisterRequest;
import com.hopital.urgences.model.AccountStatus;
import com.hopital.urgences.model.user_role.Role;
import com.hopital.urgences.model.user_role.User;
import com.hopital.urgences.repository.UserRepository;
import com.hopital.urgences.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Set<Role> ROLES_INSCRIPTIBLES = Set.of(Role.MEDECIN, Role.LABO_IMAGERIE, Role.RECEPTIONIST);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void register(RegisterRequest request) {
        if (!ROLES_INSCRIPTIBLES.contains(request.getRole())) {
            throw new IllegalStateException("Ce rôle ne peut pas être demandé par inscription publique.");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Un compte existe déjà avec cet email.");
        }

        User user = User.builder()
                .nom(request.getNom())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .statut(AccountStatus.PENDING)
                .disponible(true)
                .build();

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email ou mot de passe incorrect"));

        if (user.getStatut() == AccountStatus.PENDING) {
            throw new IllegalStateException("Ce compte est en attente de validation par un administrateur.");
        }
        if (user.getStatut() == AccountStatus.REJECTED) {
            throw new IllegalStateException("Cette demande de compte a été refusée. Contactez un administrateur.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (Exception e) {
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());

        return new LoginResponse(token, user.getId(), user.getNom(), user.getRole());
    }
}