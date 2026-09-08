package com.hopital.urgences.dto.login;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String nom;
    private String nouveauMotDePasse;
}