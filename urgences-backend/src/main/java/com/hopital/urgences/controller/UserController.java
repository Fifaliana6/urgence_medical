package com.hopital.urgences.controller;

import com.hopital.urgences.dto.DisponibiliteRequest;
import com.hopital.urgences.dto.login.ProfileUpdateRequest;
import com.hopital.urgences.dto.login.UserDTO;
import com.hopital.urgences.security.CustomUserDetails;
import com.hopital.urgences.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserDTO getProfil(@AuthenticationPrincipal CustomUserDetails auth) {
        return userService.getProfile(auth.getUser().getId());
    }

    @PutMapping("/me")
    public UserDTO modifierProfil(@Valid @RequestBody ProfileUpdateRequest request,
                                   @AuthenticationPrincipal CustomUserDetails auth) {
        return userService.updateProfile(auth.getUser().getId(), request);
    }

    @PutMapping("/me/disponibilite")
    public UserDTO changerMaDisponibilite(@Valid @RequestBody DisponibiliteRequest request,
                                           @AuthenticationPrincipal CustomUserDetails auth) {
        return userService.changerDisponibilite(auth.getUser().getId(), request.getDisponible());
    }

    @GetMapping("/medecins-disponibles")
    public List<UserDTO> medecinsDisponibles() {
        return userService.listerMedecinsDisponibles();
    }
}