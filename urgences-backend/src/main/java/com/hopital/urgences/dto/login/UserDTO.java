package com.hopital.urgences.dto.login;

import com.hopital.urgences.model.AccountStatus;
import com.hopital.urgences.model.user_role.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String nom;
    private String email;
    private Role role;
    private AccountStatus statut;
    private boolean disponible;
}