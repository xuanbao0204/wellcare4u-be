package vn.wellcare4u.controllers.doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.request.DoctorProfileRequest;
import vn.wellcare4u.services.DoctorService;

@RestController
@RequestMapping("/api/v1/doctor/profile")
public class UpdateInfoDoctor {

	@Autowired
	private DoctorService doctorServ;
	
	@GetMapping("")
	public ApiResponse<?> getDoctorProfile(Authentication authentication) {
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
	            .data(doctorServ.getDoctorProfile(email))
	            .build();
	}
	
	@PutMapping("")
	public ApiResponse<?> updateDoctorProfile(
	        Authentication authentication,
	        @RequestBody DoctorProfileRequest request
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
	            .data(doctorServ.updateDoctorProfile(email, request))
	            .build();
	}
}
