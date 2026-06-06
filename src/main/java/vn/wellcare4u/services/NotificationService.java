package vn.wellcare4u.services;

import java.util.List;

import vn.wellcare4u.entities.NotificationRecipient;
import vn.wellcare4u.enums.ENotificationTarget;
import vn.wellcare4u.enums.ERole;
import vn.wellcare4u.models.dto.NotificationDTO;
import vn.wellcare4u.models.request.NotificationRequest;

public interface NotificationService {

	void markAsRead(Long recipientId, String email);

	long countUnread(String email);

	List<NotificationDTO> getMyNotifications(String email);

	void send(NotificationRequest req, String sender);

	NotificationDTO mapToDTO(NotificationRecipient r);

	long countByTargetOrRole(ENotificationTarget target, ERole role);

	void send(NotificationRequest req);

//	void markAsRead(Long id, String email);
//
//	long countUnread(String email);
//
//	List<NotificationDTO> getMyNotifications(String email);
//
//	void sendNotification(NotificationRequest req);
//
//	void sendNotificationToGroup(NotificationRequest req);

}
