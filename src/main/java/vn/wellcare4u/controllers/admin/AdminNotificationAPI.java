package vn.wellcare4u.controllers.admin;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.wellcare4u.enums.ENotificationTarget;
import vn.wellcare4u.enums.ERole;
import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.request.NotificationRequest;
import vn.wellcare4u.services.NotificationService;
import vn.wellcare4u.services.UserService;

@RestController
@RequestMapping("/api/v1/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationAPI {

    private final NotificationService notificationService;
    private final UserService uServ;

    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> send(
            @RequestBody @Valid NotificationRequest req, Authentication auth) {

        req.validate();
        notificationService.send(req, auth.getName());

        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Gửi thông báo thành công")
                .build();
    }
    
    @PostMapping("/get-recipients")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<Long, String>> getRecipients(Authentication auth) {
    	
    	if (auth == null || !auth.isAuthenticated()) {
    		return  ApiResponse.<Map<Long, String>>builder()
                    .status(401)
                    .message("Not authorized")
                    .build();
    	}
    	
        return ApiResponse.<Map<Long, String>>builder()
                .status(HttpStatus.OK.value())
                .data(uServ.getUserIds())
                .message("Gửi thông báo thành công")
                .build();
    }
    
    @GetMapping("/preview")
    public ApiResponse<Long> preview(
            @RequestParam ENotificationTarget target,
            @RequestParam(required = false) ERole role) {

        return ApiResponse.<Long>builder()
        		.status(200)
        		.message("Success")
        		.data(notificationService.countByTargetOrRole(target, role))
        		.build();
    }
}