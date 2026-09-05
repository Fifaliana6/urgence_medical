// dto/RegisterUserRequest.java
package com.hopital.urgences.dto;

import com.hopital.urgences.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterUserRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotNull UserRole role,
        String nom,
        String prenom
) {}