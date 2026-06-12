package vn.wellcare4u.controllers.user;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.dto.NotificationDTO;
import vn.wellcare4u.services.NotificationService;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationAPI {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<NotificationDTO>> getMyNotifications(Principal principal) {
        return ApiResponse.<List<NotificationDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách thông báo thành công")
                .data(notificationService.getMyNotifications(principal.getName()))
                .build();
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> countUnread(Principal principal) {
        return ApiResponse.<Long>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy số thông báo chưa đọc thành công")
                .data(notificationService.countUnread(principal.getName()))
                .build();
    }

    // Path variable is now recipientId, not notificationId
    @PostMapping("/{recipientId}/read")
    public ApiResponse<Void> markAsRead(
            @PathVariable Long recipientId,
            Principal principal) {
        notificationService.markAsRead(recipientId, principal.getName());
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Đánh dấu đã đọc thành công")
                .build();
    }
    
    @GetMapping("/by-sender")
    public ApiResponse<List<NotificationDTO>> getMyNotificationsBySender(Principal principal) {
        return ApiResponse.<List<NotificationDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách thông báo thành công")
                .data(notificationService.getNotificationsBySender(principal.getName()))
                .build();
    }
}