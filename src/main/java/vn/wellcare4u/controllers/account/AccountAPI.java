package vn.wellcare4u.controllers.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.request.ChangePasswordRequest;
import vn.wellcare4u.models.request.DeleteAccountRequest;
import vn.wellcare4u.models.request.EmailAccountRequest;
import vn.wellcare4u.services.AccountService;

@RestController
@RequestMapping("/api/v1/account")
public class AccountAPI {

	@Autowired
	AccountService accServ;
	
	@PutMapping("/{id}/active")
	public ApiResponse<Void> activeAccount(@PathVariable Long id) {
		accServ.activeAccount(id);
		return ApiResponse.<Void>builder()
				.status(200)
				.message("Cập nhật thành công")
				.build();
	}
	
	@PutMapping("/activate")
	public ApiResponse<Void> activateAccount(@RequestBody EmailAccountRequest req) {
		accServ.activateAccount(req.getEmail());
		return ApiResponse.<Void>builder()
				.status(200)
				.message("Cập nhật thành công")
				.build();
	}
	
	
	@PutMapping("/{id}/de-active")
	public ApiResponse<Void> deActiveAccount(@PathVariable Long id) {
		accServ.deActiveAccount(id);
		return ApiResponse.<Void>builder()
				.status(200)
				.message("Cập nhật thành công")
				.build();
	}
	
	@PutMapping("/deactivate")
	public ApiResponse<Void> deactivateAccount(@RequestBody EmailAccountRequest req) {
		accServ.deactivateAccount(req.getEmail());
		return ApiResponse.<Void>builder()
				.status(200)
				.message("Cập nhật thành công")
				.build();
	}
	
	@DeleteMapping("/{id}/delete")
	public ApiResponse<Void> deleteAccount(@PathVariable Long id) {
		accServ.deleteAccount(id);
		return ApiResponse.<Void>builder()
				.status(200)
				.message("Cập nhật thành công")
				.build();
	}
	
	@PutMapping("/change-password")
	public ApiResponse<Void> changePassword(@RequestBody ChangePasswordRequest req, Authentication auth) {
		
		if (auth == null || !auth.isAuthenticated()) {
		    return ApiResponse.<Void>builder()
		            .status(401)
		            .message("Chưa đăng nhập")
		            .build();
		}
		
		accServ.changePassword(req.getCurrentPassword(), req.getNewPassword(), auth.getName());
		return ApiResponse.<Void>builder()
	            .status(200)
	            .message("Đổi mật khẩu thành công")
	            .build();
	}
	
	@PutMapping("/delete")
	public ApiResponse<Void> deleteAccount(Authentication auth, @RequestBody DeleteAccountRequest req) {
		
		if (auth == null || !auth.isAuthenticated()) {
		    return ApiResponse.<Void>builder()
		            .status(401)
		            .message("Chưa đăng nhập")
		            .build();
		}
		
		accServ.deleteAccount(auth.getName(), req.getPassword());
		return ApiResponse.<Void>builder()
	            .status(200)
	            .message("Xóa tài khoản thành công")
	            .build();
	}
}
