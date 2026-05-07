package vn.wellcare4u.controllers.patient.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.wellcare4u.enums.EAppointmentStatus;
import vn.wellcare4u.enums.EAppointmentType;
import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.dto.AppointmentDTO;
import vn.wellcare4u.models.request.AppointmentRequest;
import vn.wellcare4u.models.request.CancelAppointmentRequest;
import vn.wellcare4u.services.AppointmentService;
import vn.wellcare4u.services.UserService;

@RestController
@RequestMapping("/api/v1/appointment")
public class PatientAppointmentAPI {
	@Autowired
	private UserService uServ;

	@Autowired
	private AppointmentService apptServ;
	
	@GetMapping("/by-patient")
	public ApiResponse<Page<AppointmentDTO>> getMyAppointment(
	        Authentication authentication,
	        @RequestParam int page,
	        @RequestParam int size,
	        @RequestParam(required = false) EAppointmentStatus status,
	        @RequestParam(required = false) EAppointmentType type,
	        @RequestParam(defaultValue = "createdAt") String sortBy,
	        @RequestParam(defaultValue = "desc") String sortDir
	) {	
		if (authentication == null || !authentication.isAuthenticated()) {
		    return ApiResponse.<Page<AppointmentDTO>>builder()
		            .status(401)
		            .message("Chưa đăng nhập")
		            .build();
		}
	    
	    Sort sort = sortDir.equalsIgnoreCase("asc")
	            ? Sort.by(sortBy).ascending()
	            : Sort.by(sortBy).descending();

	    Pageable pageable = PageRequest.of(page, size, sort);

	    Page<AppointmentDTO> result =
	            apptServ.getAppointmentByPatient(authentication.getName(), status, type, pageable);

	    return ApiResponse.<Page<AppointmentDTO>>builder()
	            .status(200)
	            .message("Lấy danh sách lịch hẹn thành công")
	            .data(result)
	            .build();
	}

	@PostMapping("/book")
	public ApiResponse<?> book(Authentication authentication, @RequestBody AppointmentRequest req) {

		if (authentication == null || !authentication.isAuthenticated()) {
			return ApiResponse.builder()
					.status(401)
					.message("Unauthorized")
					.build();
		}

		Long patientId = uServ.getIdFromEmail(authentication.getName());

		AppointmentDTO result = apptServ.bookSlot(req, patientId);

		return ApiResponse.builder()
				.status(200)
				.message("Đặt lịch thành công")
				.data(result)
				.build();
	}
	
	@PutMapping("/check-in/{appointmentId}") 
	public ApiResponse<Void> checkInAppointment(Authentication authentication, @PathVariable Long appointmentId){
		
		Long patientId = uServ.getIdFromEmail(authentication.getName());
		apptServ.checkIn(patientId, appointmentId);
		
		return ApiResponse.<Void>builder()
				.status(200)
				.message("Check-in thành công")
				.build();
	}
}
