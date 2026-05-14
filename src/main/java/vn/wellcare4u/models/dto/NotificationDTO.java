package vn.wellcare4u.models.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import vn.wellcare4u.enums.ENotificationType;

@Data
@Builder

public class NotificationDTO {
	private Long id;
	private Long recipientId;
	private Long notificationId;
	private String title;
	private String content;
	private ENotificationType type;
	private Long referenceId;
	private Boolean isRead;
	private LocalDateTime createdAt;
}

//public class NotificationDTO {
//
//    private Long id;
//    private String title;
//    private String content;
//    private Boolean isRead;
//    private LocalDateTime createdAt;
//    private ENotificationType type;
//    private Long referenceId;
//}