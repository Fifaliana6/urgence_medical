package com.hopital.urgences.dto.salle_lit;

import com.hopital.urgences.model.salle.TypeSalle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalleDTO {
    private Long id;
    private String nom;
    private TypeSalle type;
    private boolean disponible;
}