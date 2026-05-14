package vn.wellcare4u.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.dto.admin.AdminAccountDTO;
import vn.wellcare4u.services.AccountService;

@RestController
@RequestMapping("/api/v1/admin/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAccountAPI {

    private final vn.wellcare4u.services.AdminService adminService;
    private final AccountService accountService;

    /**
     * GET /api/v1/admin/accounts
     * Lấy danh sách tài khoản, lọc theo role / status / keyword
     */
    @GetMapping
    public ApiResponse<Page<AdminAccountDTO>> getAccounts(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.<Page<AdminAccountDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách tài khoản thành công")
                .data(adminService.getAccounts(role, status, keyword, page, size))
                .build();
    }

    /**
     * PUT /api/v1/admin/accounts/{id}/active
     * Kích hoạt tài khoản
     */
    @PutMapping("/{id}/active")
    public ApiResponse<Void> activeAccount(@PathVariable Long id) {
        accountService.activeAccount(id);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Kích hoạt tài khoản thành công")
                .build();
    }

    /**
     * PUT /api/v1/admin/accounts/{id}/lock
     * Khoá tài khoản
     */
    @PutMapping("/{id}/lock")
    public ApiResponse<Void> lockAccount(@PathVariable Long id) {
        accountService.lockAccount(id);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Khoá tài khoản thành công")
                .build();
    }

    /**
     * DELETE /api/v1/admin/accounts/{id}
     * Xoá tài khoản (soft delete)
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Xoá tài khoản thành công")
                .build();
    }

    /**
     * PUT /api/v1/admin/accounts/{id}/verify-doctor
     * Xác minh bác sĩ
     */
    @PutMapping("/{id}/verify-doctor")
    public ApiResponse<Void> verifyDoctor(@PathVariable Long id) {
        adminService.verifyDoctor(id);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Xác minh bác sĩ thành công")
                .build();
    }

    /**
     * PUT /api/v1/admin/accounts/{id}/unverify-doctor
     * Hủy xác minh bác sĩ
     */
    @PutMapping("/{id}/unverify-doctor")
    public ApiResponse<Void> unverifyDoctor(@PathVariable Long id) {
        adminService.unverifyDoctor(id);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Hủy xác minh bác sĩ thành công")
                .build();
    }
}