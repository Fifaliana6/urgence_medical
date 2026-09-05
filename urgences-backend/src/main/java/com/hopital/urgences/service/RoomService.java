// service/RoomService.java
package com.hopital.urgences.service;

import com.hopital.urgences.model.*;
import com.hopital.urgences.repository.EmergencyVisitRepository;
import com.hopital.urgences.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final EmergencyVisitRepository visitRepository;

    @Transactional
    public void reserveDechocageRoom(Long visitId) {
        EmergencyVisit visite = visitRepository.findById(visitId).orElseThrow();
        roomRepository.findAll().stream()
                .filter(r -> r.getType() == RoomType.DECHOCAGE && r.isDisponible())
                .findFirst()
                .ifPresent(room -> {
                    room.setDisponible(false);
                    visite.setSalle(room);
                    roomRepository.save(room);
                    visitRepository.save(visite);
                });
    }
}