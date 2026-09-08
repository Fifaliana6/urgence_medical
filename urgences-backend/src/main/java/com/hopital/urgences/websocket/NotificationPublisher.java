package com.hopital.urgences.websocket;

import com.hopital.urgences.dto.NotificationDTO;
import com.hopital.urgences.model.Notification;
import com.hopital.urgences.model.user_role.Role;
import com.hopital.urgences.repository.NotificationRepository;
import com.hopital.urgences.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void notifierRole(Role role, String message, Long visitId) {
        Notification notification = Notification.builder()
                .destinataireRole(role)
                .message(message)
                .visitId(visitId)
                .lu(false)
                .dateCreation(LocalDateTime.now())
                .build();
        notification = notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/role/" + role.name(), toDTO(notification));
    }

    public void notifierUtilisateur(Long userId, String message, Long visitId) {
        Notification notification = Notification.builder()
                .destinataireUser(userRepository.getReferenceById(userId))
                .message(message)
                .visitId(visitId)
                .lu(false)
                .dateCreation(LocalDateTime.now())
                .build();
        notification = notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/user/" + userId, toDTO(notification));
    }

    private NotificationDTO toDTO(Notification n) {
        return new NotificationDTO(n.getId(), n.getMessage(), n.getVisitId(), n.isLu(), n.getDateCreation());
    }
}