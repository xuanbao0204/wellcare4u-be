package vn.wellcare4u.controllers.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.wellcare4u.models.ApiResponse;

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
	
	@PutMapping("/{id}/de-active")
	public ApiResponse<Void> deActiveAccount(@PathVariable Long id) {
		accServ.deActiveAccount(id);
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
}
