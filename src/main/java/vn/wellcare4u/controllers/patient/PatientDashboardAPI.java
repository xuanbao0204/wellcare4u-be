package vn.wellcare4u.controllers.patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.dto.patient.PatientDashboardDTO;
import vn.wellcare4u.services.PatientService;

@RestController
@RequestMapping("/api/v1/patient")
public class PatientDashboardAPI {

	@Autowired
	private PatientService patientServ;
	
	@GetMapping("/dashboard")
	public ApiResponse<?> getPatientDashboard(Authentication auth) {
		if (auth == null || !auth.isAuthenticated()) {
		    return ApiResponse.builder()
		            .status(401)
		            .message("Chưa đăng nhập")
		            .build();
		}
		
		return ApiResponse.<PatientDashboardDTO>builder()
				.status(200)
				.data(patientServ.getDashboard(auth.getName()))
				.message("Get info successfully")
				.build();
	}
}
