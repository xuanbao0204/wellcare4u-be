package vn.wellcare4u.models.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import vn.wellcare4u.entities.User;
import vn.wellcare4u.enums.ENotificationTarget;
import vn.wellcare4u.enums.ENotificationType;
import vn.wellcare4u.enums.ERole;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NonNull
    private ENotificationTarget target;

    private Long receiverId;

    private List<Long> receiverIds;

    private ERole role;

    @NonNull
    private ENotificationType type;

    @NonNull
    private String title;

    @NonNull
    private String content;

    // Optional — links the notification to a specific entity (appointment, record, etc.)
    private Long referenceId;

    // ── Factory methods — callers use these instead of the builder directly ───

    public static NotificationRequest toUser(
            Long userId,
            ENotificationType type,
            String title,
            String content,
            Long referenceId) {
        return NotificationRequest.builder()
                .target(ENotificationTarget.SINGLE)
                .receiverId(userId)
                .type(type)
                .title(title)
                .content(content)
                .referenceId(referenceId)
                .build();
    }

    public static NotificationRequest toUsers(
            List<Long> userIds,
            ENotificationType type,
            String title,
            String content,
            Long referenceId) {
        return NotificationRequest.builder()
                .target(ENotificationTarget.IDS)
                .receiverIds(userIds)
                .type(type)
                .title(title)
                .content(content)
                .referenceId(referenceId)
                .build();
    }

    public static NotificationRequest toRole(
            ERole role,
            ENotificationType type,
            String title,
            String content) {
        return NotificationRequest.builder()
                .target(ENotificationTarget.ROLE)
                .role(role)
                .type(type)
                .title(title)
                .content(content)
                .build();
    }

    public static NotificationRequest broadcast(
            ENotificationType type,
            String title,
            String content) {
        return NotificationRequest.builder()
                .target(ENotificationTarget.BROADCAST)
                .type(type)
                .title(title)
                .content(content)
                .build();
    }

    // ── Validation — called by the service before processing ─────────────────

    public void validate() {
        switch (target) {
            case SINGLE -> {
                if (receiverId == null)
                    throw new IllegalArgumentException("receiverId is required for SINGLE target");
            }
            case IDS -> {
                if (receiverIds == null || receiverIds.isEmpty())
                    throw new IllegalArgumentException("receiverIds must not be empty for IDS target");
            }
            case ROLE -> {
                if (role == null)
                    throw new IllegalArgumentException("role is required for ROLE target");
            }
            case BROADCAST -> { /* no extra fields needed */ }
        }
    }
}

//@Builder
//@Data
//public class NotificationRequest {
//    private User receiver;
//    private ENotificationType type;
//    private String title;
//    private String content;
//    private Long referenceId;
//}