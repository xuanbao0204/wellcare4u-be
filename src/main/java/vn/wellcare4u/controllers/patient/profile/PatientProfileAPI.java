package vn.wellcare4u.controllers.patient.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.request.PatientProfileRequest;
import vn.wellcare4u.services.PatientService;

@RestController
@RequestMapping("/api/v1/patient/profile")
public class PatientProfileAPI {

	@Autowired
	private PatientService patientServ;
	
	@GetMapping("")
	public ApiResponse<?> getPatientProfile(Authentication authentication) {
	    if (authentication == null || !authentication.isAuthenticated()) {
	        return ApiResponse.builder()
	                .status(401)
	                .message("Unauthorized")
	                .build();
	    }

	    String email = authentication.getName();

	    return ApiResponse.builder()
	            .status(200)
	            .message("Doctor profile retrieved successfully")
	            .data(patientServ.getPatientProfile(email))
	            .build();
	}
	
	@PutMapping("")
	public ApiResponse<?> updatePatientProfile(
	        Authentication authentication,
	        @RequestBody PatientProfileRequest request
	) {
	    if (authentication == null || !authentication.isAuthenticated()) {
	        return ApiResponse.builder()
	                .status(401)
	                .message("Unauthorized")
	                .build();
	    }

	    String email = authentication.getName();

	    return ApiResponse.builder()
	            .status(200)
	            .message("Doctor profile updated successfully")
	            .data(patientServ.updatePatientProfile(email, request))
	            .build();
	}
}
