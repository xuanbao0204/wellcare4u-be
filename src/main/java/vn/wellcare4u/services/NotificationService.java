package vn.wellcare4u.services;

import java.util.List;

import jakarta.transaction.Transactional;
import vn.wellcare4u.entities.Account;
import vn.wellcare4u.models.dto.NotificationDTO;
import vn.wellcare4u.models.request.NotificationRequest;

public interface NotificationService {

	void markAsRead(Long recipientId, String email);

	long countUnread(String email);

	List<NotificationDTO> getMyNotifications(String email);

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
