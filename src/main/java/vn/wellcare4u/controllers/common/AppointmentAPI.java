package vn.wellcare4u.controllers.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.request.CancelAppointmentRequest;
import vn.wellcare4u.services.AppointmentService;
import vn.wellcare4u.services.UserService;

@RestController
@RequestMapping("/api/v1/appointment")
public class AppointmentAPI {

	@Autowired
	private UserService uServ;

	@Autowired
	private AppointmentService apptServ;
	
	@PutMapping("/cancel/{appointmentId}")
	public ApiResponse<Void> cancelAppointment(Authentication authentication, @PathVariable Long appointmentId, @RequestBody CancelAppointmentRequest cancelReq){
		
		Long userId = uServ.getIdFromEmail(authentication.getName());
		apptServ.cancel(userId, appointmentId, cancelReq);
		
		return ApiResponse.<Void>builder()
				.status(200)
				.message("Hủy thành công")
				.build();
	}
}
