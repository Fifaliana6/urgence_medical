package com.hopital.urgences.service;

import com.hopital.urgences.dto.login.ProfileUpdateRequest;
import com.hopital.urgences.dto.login.UserDTO;
import com.hopital.urgences.exception.ResourceNotFoundException;
import com.hopital.urgences.model.AccountStatus;
import com.hopital.urgences.model.user_role.Role;
import com.hopital.urgences.model.user_role.User;
import com.hopital.urgences.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public UserDTO getProfile(Long userId) {
        return toDTO(findEntity(userId));
    }

    @Transactional
    public UserDTO updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = findEntity(userId);

        if (request.getNom() != null && !request.getNom().isBlank()) {
            user.setNom(request.getNom());
        }
        if (request.getNouveauMotDePasse() != null && !request.getNouveauMotDePasse().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getNouveauMotDePasse()));
        }

        return toDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO changerDisponibilite(Long userId, boolean disponible) {
        User user = findEntity(userId);
        user.setDisponible(disponible);
        return toDTO(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserDTO> listerMedecinsDisponibles() {
        return userRepository.findByRoleAndStatutAndDisponibleTrue(Role.MEDECIN, AccountStatus.APPROVED)
                .stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<UserDTO> listerUtilisateurs(AccountStatus statutFiltre) {
        List<User> users = statutFiltre != null ? userRepository.findByStatut(statutFiltre) : userRepository.findAll();
        return users.stream().map(this::toDTO).toList();
    }

    @Transactional
    public UserDTO approuver(Long userId, User admin) {
        User user = findEntity(userId);
        user.setStatut(AccountStatus.APPROVED);
        user = userRepository.save(user);
        auditService.enregistrer(admin, "APPROBATION_COMPTE", user.getId(),
                "Compte approuvé : " + user.getEmail() + " (" + user.getRole() + ")");
        return toDTO(user);
    }

    @Transactional
    public UserDTO rejeter(Long userId, User admin) {
        User user = findEntity(userId);
        user.setStatut(AccountStatus.REJECTED);
        user = userRepository.save(user);
        auditService.enregistrer(admin, "REJET_COMPTE", user.getId(),
                "Compte rejeté : " + user.getEmail() + " (" + user.getRole() + ")");
        return toDTO(user);
    }

    public User findEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable, id=" + id));
    }

    private UserDTO toDTO(User u) {
        return new UserDTO(u.getId(), u.getNom(), u.getEmail(), u.getRole(), u.getStatut(), u.isDisponible());
    }
}