// controller/DoctorController.java
package com.hopital.urgences.controller;

import com.hopital.urgences.model.Doctor;
import com.hopital.urgences.model.DoctorStatus;
import com.hopital.urgences.repository.DoctorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorRepository doctorRepository;

    @PostMapping
    public ResponseEntity<Doctor> creer(@RequestBody Doctor doctor) {
        doctor.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorRepository.save(doctor));
    }

    @GetMapping
    public List<Doctor> lister() {
        return doctorRepository.findAll();
    }

    @GetMapping("/disponibles")
    public List<Doctor> disponibles() {
        return doctorRepository.findAll().stream()
                .filter(d -> d.getStatut() == DoctorStatus.DISPONIBLE)
                .toList();
    }

    @PutMapping("/{id}")
    public Doctor modifier(@PathVariable Long id, @RequestBody Doctor requete) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Médecin introuvable: " + id));
        doctor.setNom(requete.getNom());
        doctor.setPrenom(requete.getPrenom());
        doctor.setSpecialite(requete.getSpecialite());
        doctor.setStatut(requete.getStatut());
        doctor.setTelephone(requete.getTelephone());
        return doctorRepository.save(doctor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        doctorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}