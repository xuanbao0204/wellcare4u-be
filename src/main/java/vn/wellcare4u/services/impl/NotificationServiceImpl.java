package vn.wellcare4u.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.wellcare4u.entities.Notification;
import vn.wellcare4u.entities.NotificationRecipient;
import vn.wellcare4u.entities.User;
import vn.wellcare4u.enums.ENotificationTarget;
import vn.wellcare4u.entities.Account;
import vn.wellcare4u.models.dto.NotificationDTO;
import vn.wellcare4u.models.request.NotificationRequest;
import vn.wellcare4u.repositories.NotificationRepository;
import vn.wellcare4u.repositories.UserRepository;
import vn.wellcare4u.repositories.AccountRepository;
import vn.wellcare4u.repositories.NotificationRecipientRepository;
import vn.wellcare4u.services.NotificationService;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepo;
    private final NotificationRecipientRepository recipientRepo;
    private final UserRepository userRepo;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void send(NotificationRequest req) {
        Notification notification = notificationRepo.save(
            Notification.builder()
                .type(req.getType())
                .title(req.getTitle())
                .content(req.getContent())
                .referenceId(req.getReferenceId())
                .build()
        );

        List<User> receivers = resolveReceivers(req);

        List<NotificationRecipient> recipients = receivers.stream()
            .map(user -> NotificationRecipient.builder()
                .notification(notification)
                .user(user)
                .isRead(false)
                .build())
            .toList();

        recipientRepo.saveAll(recipients);

        if (req.getTarget() == ENotificationTarget.BROADCAST) {
            messagingTemplate.convertAndSend(
                "/topic/notifications",
                mapToDTO(recipients.get(0))
            );
        } else {
            recipients.forEach(r ->
                messagingTemplate.convertAndSendToUser(
                    r.getUser().getAccount().getEmail(),
                    "/queue/notifications",
                    mapToDTO(r)
                )
            );
        }
    }

    private List<User> resolveReceivers(NotificationRequest req) {
        return switch (req.getTarget()) {
            case SINGLE    -> List.of(
                                userRepo.findById(req.getReceiverId())
                                    .orElseThrow(() -> new RuntimeException("User not found"))
                              );
            case IDS       -> userRepo.findAllById(req.getReceiverIds());
            case ROLE      -> userRepo.findAllByAccount_Role(req.getRole());
            case BROADCAST -> userRepo.findAll();
        };
    }

    @Override
    public List<NotificationDTO> getMyNotifications(String email) {
        User user = findUserByEmail(email);
        return recipientRepo.findByUserId(user.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public long countUnread(String email) {
        User user = findUserByEmail(email);
        return recipientRepo.countUnread(user.getId());
    }

    @Override
    @Transactional
    public void markAsRead(Long recipientId, String email) {
        User user = findUserByEmail(email);
        NotificationRecipient r = recipientRepo
            .findByIdAndUserId(recipientId, user.getId())
            .orElseThrow(() -> new AccessDeniedException("Forbidden"));

        r.setIsRead(true);
        recipientRepo.save(r);
    }

    private User findUserByEmail(String email) {
        return userRepo.findByAccount_Email(email)
            .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    @Override
	public NotificationDTO mapToDTO(NotificationRecipient r) {
        Notification n = r.getNotification();
        return NotificationDTO.builder()
        		.id(r.getId())
                .recipientId(r.getId())
                .notificationId(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .type(n.getType())
                .referenceId(n.getReferenceId())
                .isRead(r.getIsRead())
                .createdAt(r.getCreatedAt())
                .build();
    }
}

//@Service
//@RequiredArgsConstructor
//public class NotificationServiceImpl implements NotificationService {
//
//    private final NotificationRepository notificationRepo;
//    private final AccountRepository accountRepo;
//    private final SimpMessagingTemplate messagingTemplate;
//
//    @Override
//    public void sendNotification(NotificationRequest req) {
//
//        Notification noti = new Notification();
//        noti.setReceiver(req.getReceiver());
//        noti.setTitle(req.getTitle());
//        noti.setContent(req.getContent());
//        noti.setType(req.getType());
//        noti.setIsRead(false);
//        noti.setCreatedAt(LocalDateTime.now());
//        noti.setReferenceId(req.getReferenceId());
//
//        notificationRepo.save(noti);
//
//        NotificationDTO dto = mapToDTO(noti);
//
//        messagingTemplate.convertAndSendToUser(
//                req.getReceiver().getAccount().getEmail(),
//                "/queue/notifications",
//                dto
//        );
//    }
//
//    @Override
//    public List<NotificationDTO> getMyNotifications(String email) {
//        Account acc = accountRepo.findByEmail(email).orElseThrow();
//        return notificationRepo
//                .findByReceiver_IdOrderByCreatedAtDesc(acc.getUser().getId())
//                .stream()
//                .map(this::mapToDTO)
//                .toList();
//    }
//
//    @Override
//    public long countUnread(String email) {
//        Account acc = accountRepo.findByEmail(email).orElseThrow();
//        return notificationRepo.countUnread(acc.getUser().getId());
//    }
//
//    @Override
//    public void markAsRead(Long id, String email) {
//        Notification noti = notificationRepo.findById(id).orElseThrow();
//
//        if (!noti.getReceiver().getAccount().getEmail().equals(email)) {
//            throw new RuntimeException("Forbidden");
//        }
//
//        noti.setIsRead(true);
//        notificationRepo.save(noti);
//    }
//
//    private NotificationDTO mapToDTO(Notification n) {
//        return NotificationDTO.builder()
//                .id(n.getId())
//                .title(n.getTitle())
//                .content(n.getContent())
//                .isRead(n.getIsRead())
//                .createdAt(n.getCreatedAt())
//                .type(n.getType())
//                .referenceId(n.getReferenceId())
//                .build();
//    }
//}