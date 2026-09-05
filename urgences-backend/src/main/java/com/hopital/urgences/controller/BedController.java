// controller/BedController.java
package com.hopital.urgences.controller;

import com.hopital.urgences.model.Bed;
import com.hopital.urgences.repository.BedRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/beds")
@RequiredArgsConstructor
public class BedController {

    private final BedRepository bedRepository;

    @PostMapping
    public ResponseEntity<Bed> creer(@RequestBody Bed bed) {
        bed.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(bedRepository.save(bed));
    }

    @GetMapping
    public List<Bed> lister() {
        return bedRepository.findAll();
    }

    @GetMapping("/disponibles")
    public List<Bed> disponibles(@RequestParam String service) {
        return bedRepository.findByServiceAndOccupeFalse(service);
    }

    @PutMapping("/{id}/liberer")
    public Bed liberer(@PathVariable Long id) {
        Bed bed = bedRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lit introuvable: " + id));
        bed.setOccupe(false);
        return bedRepository.save(bed);
    }
}