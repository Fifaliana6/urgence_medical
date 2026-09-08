package com.hopital.urgences.controller;

import com.hopital.urgences.dto.login.UserDTO;
import com.hopital.urgences.model.AccountStatus;
import com.hopital.urgences.security.CustomUserDetails;
import com.hopital.urgences.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public List<UserDTO> lister(@RequestParam(required = false) AccountStatus statut) {
        return userService.listerUtilisateurs(statut);
    }

    @PutMapping("/{id}/approuver")
    public UserDTO approuver(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails admin) {
        return userService.approuver(id, admin.getUser());
    }

    @PutMapping("/{id}/rejeter")
    public UserDTO rejeter(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails admin) {
        return userService.rejeter(id, admin.getUser());
    }
}