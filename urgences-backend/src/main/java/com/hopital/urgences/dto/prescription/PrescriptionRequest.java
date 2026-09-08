package com.hopital.urgences.dto.prescription;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class PrescriptionRequest {
    @NotEmpty
    @Valid
    private List<PrescriptionItemRequest> items;
}