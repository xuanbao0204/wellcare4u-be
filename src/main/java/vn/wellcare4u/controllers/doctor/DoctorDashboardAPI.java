package vn.wellcare4u.controllers.doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.dto.doctor.DoctorDashboardSnapshotDTO;
import vn.wellcare4u.services.DoctorService;

@RestController
@RequestMapping("/api/v1/doctor")
public class DoctorDashboardAPI {

	@Autowired
	private DoctorService doctorServ;
	
	@GetMapping("/dashboard")
	public ApiResponse<?> getDoctorDashboard(Authentication auth) {
		if (auth == null || !auth.isAuthenticated()) {
		    return ApiResponse.builder()
		            .status(401)
		            .message("Chưa đăng nhập")
		            .build();
		}
		
		return ApiResponse.<DoctorDashboardSnapshotDTO>builder()
				.status(200)
				.data(doctorServ.getDashboard(auth.getName()))
				.message("Get info successfully")
				.build();
	}
}
