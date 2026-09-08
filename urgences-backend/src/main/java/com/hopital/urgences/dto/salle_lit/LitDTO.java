package com.hopital.urgences.dto.salle_lit;

import com.hopital.urgences.model.ServiceMedical;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LitDTO {
    private Long id;
    private String numero;
    private ServiceMedical service;
    private boolean occupe;
}