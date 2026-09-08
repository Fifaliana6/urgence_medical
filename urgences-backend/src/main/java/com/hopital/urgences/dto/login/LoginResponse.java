package com.hopital.urgences.dto.login;

import com.hopital.urgences.model.user_role.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long userId;
    private String nom;
    private Role role;
}