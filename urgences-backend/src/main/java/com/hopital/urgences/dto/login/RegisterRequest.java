package com.hopital.urgences.dto.login;

import com.hopital.urgences.model.user_role.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String nom;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;

    @NotNull
    private Role role;
}