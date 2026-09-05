// repository/RoomRepository.java
package com.hopital.urgences.repository;

import com.hopital.urgences.model.Room;
import com.hopital.urgences.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByTypeAndDisponibleTrue(RoomType type);
}