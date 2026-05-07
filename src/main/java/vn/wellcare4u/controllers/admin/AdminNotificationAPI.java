package vn.wellcare4u.controllers.admin;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.request.NotificationRequest;
import vn.wellcare4u.services.NotificationService;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationAPI {

    private final NotificationService notificationService;

    @PostMapping("/send")
    public ApiResponse<Void> send(
            @RequestBody @Valid NotificationRequest req) {

        req.validate();
        notificationService.send(req);

        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Gửi thông báo thành công")
                .build();
    }
}