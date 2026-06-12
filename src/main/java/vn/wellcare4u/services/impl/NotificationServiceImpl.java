package vn.wellcare4u.services.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.wellcare4u.entities.Notification;
import vn.wellcare4u.entities.NotificationRecipient;
import vn.wellcare4u.entities.User;
import vn.wellcare4u.entities.admin.AuditLog;
import vn.wellcare4u.enums.ENotificationTarget;
import vn.wellcare4u.enums.ERole;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.models.dto.NotificationDTO;
import vn.wellcare4u.models.request.NotificationRequest;
import vn.wellcare4u.repositories.NotificationRepository;
import vn.wellcare4u.repositories.UserRepository;
import vn.wellcare4u.repositories.NotificationRecipientRepository;
import vn.wellcare4u.services.AuditLogService;
import vn.wellcare4u.services.NotificationService;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepo;
	private final NotificationRecipientRepository recipientRepo;
	private final UserRepository userRepo;
	private final SimpMessagingTemplate messagingTemplate;
	private final AuditLogService logServ;

	@Override
	@Transactional
	public void send(NotificationRequest req) {
		Notification notification = notificationRepo.save(Notification.builder().type(req.getType())
				.title(req.getTitle()).content(req.getContent()).referenceId(req.getReferenceId()).sender(null).build());

		List<User> receivers = resolveReceivers(req);

		List<NotificationRecipient> recipients = receivers.stream().map(
				user -> NotificationRecipient.builder().notification(notification).user(user).isRead(false).build())
				.toList();

		recipientRepo.saveAll(recipients);

		if (req.getTarget() == ENotificationTarget.BROADCAST) {
			messagingTemplate.convertAndSend("/topic/notifications", mapBroadcastDTO(notification));
		} else {
			recipients.forEach(r -> messagingTemplate.convertAndSendToUser(r.getUser().getAccount().getEmail(),
					"/queue/notifications", mapToDTO(r)));
		}
		
	}
	
	@Override
	@Transactional
	public void send(NotificationRequest req, String senderId) {
		User sender = userRepo.findByAccount_Email(senderId).orElseThrow(() -> new AppException("User not found", "USER_NOT_FOUND", HttpStatus.BAD_REQUEST));
		Notification notification = notificationRepo.save(Notification.builder().type(req.getType())
				.title(req.getTitle()).content(req.getContent()).referenceId(req.getReferenceId()).sender(sender).build());

		List<User> receivers = resolveReceivers(req);

		List<NotificationRecipient> recipients = receivers.stream().map(
				user -> NotificationRecipient.builder().notification(notification).user(user).isRead(false).build())
				.toList();

		recipientRepo.saveAll(recipients);

		if (req.getTarget() == ENotificationTarget.BROADCAST) {
			messagingTemplate.convertAndSend("/topic/notifications", mapBroadcastDTO(notification));
		} else {
			recipients.forEach(r -> messagingTemplate.convertAndSendToUser(r.getUser().getAccount().getEmail(),
					"/queue/notifications", mapToDTO(r)));
		}
		
		if (sender.getAccount().getRole() == ERole.ADMIN) {
			logServ.createLog(new AuditLog(sender, "Send notification", "Notification"));
		}
	}

	private List<User> resolveReceivers(NotificationRequest req) {
		return switch (req.getTarget()) {
		case SINGLE ->
			List.of(userRepo.findById(req.getReceiverId()).orElseThrow(() -> new RuntimeException("User not found")));
		case IDS -> userRepo.findAllById(req.getReceiverIds());
		case ROLE -> userRepo.findAllByAccount_Role(req.getRole());
		case BROADCAST -> userRepo.findAll();
		};
	}

	@Override
	public List<NotificationDTO> getMyNotifications(String email) {
		User user = findUserByEmail(email);
		return recipientRepo.findByUserId(user.getId()).stream().map(this::mapToDTO).toList();
	}

	@Override
	public List<NotificationDTO> getNotificationsBySender(String email) {
		User user = findUserByEmail(email);
		return notificationRepo.findBySender_IdOrderByCreatedAtDesc(user.getId()).stream().map(this::mapToDTO).toList();
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
		NotificationRecipient r = recipientRepo.findByIdAndUserId(recipientId, user.getId())
				.orElseThrow(() -> new AccessDeniedException("Forbidden"));

		r.setIsRead(true);
		recipientRepo.save(r);
	}

	private User findUserByEmail(String email) {
		return userRepo.findByAccount_Email(email).orElseThrow(() -> new RuntimeException("User not found: " + email));
	}

	@Override
	public long countByTargetOrRole(ENotificationTarget target, ERole role) {
	    return switch (target) {
	        case ROLE -> userRepo.countByAccount_Role(role);
	        case BROADCAST -> userRepo.count();
	        default -> 0L;
	    };
	}

	private NotificationDTO mapBroadcastDTO(Notification n) {
	    return NotificationDTO.builder()
	            .notificationId(n.getId())
	            .title(n.getTitle())
	            .content(n.getContent())
	            .type(n.getType())
	            .referenceId(n.getReferenceId())
	            .createdAt(n.getCreatedAt())
	            .build();
	}
	
	private NotificationDTO mapToDTO(Notification n) {
	    return NotificationDTO.builder()
	    		.id(n.getId())
	    		.notificationId(n.getId())
				.title(n.getTitle())
				.content(n.getContent())
				.type(n.getType())
				.referenceId(n.getReferenceId())
				.createdAt(n.getCreatedAt())
				.build();
	}
	
	@Override
	public NotificationDTO mapToDTO(NotificationRecipient r) {
		Notification n = r.getNotification();
		return NotificationDTO.builder().id(r.getId()).recipientId(r.getId()).notificationId(n.getId())
				.title(n.getTitle()).content(n.getContent()).type(n.getType()).referenceId(n.getReferenceId())
				.isRead(r.getIsRead()).createdAt(r.getCreatedAt()).build();
	}
}