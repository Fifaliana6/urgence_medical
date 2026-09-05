// controller/RoomController.java
package com.hopital.urgences.controller;

import com.hopital.urgences.model.Room;
import com.hopital.urgences.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomRepository roomRepository;

    @PostMapping
    public ResponseEntity<Room> creer(@RequestBody Room room) {
        room.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomRepository.save(room));
    }

    @GetMapping
    public List<Room> lister() {
        return roomRepository.findAll();
    }

    @PutMapping("/{id}")
    public Room modifier(@PathVariable Long id, @RequestBody Room requete) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Salle introuvable: " + id));
        room.setNom(requete.getNom());
        room.setType(requete.getType());
        room.setDisponible(requete.isDisponible());
        return roomRepository.save(room);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        roomRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}