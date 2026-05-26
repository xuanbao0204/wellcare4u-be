package vn.wellcare4u.controllers.doctor.appointment;

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
public class DoctorAppointmentAPI {
	@Autowired
	private UserService uServ;

	@Autowired
	private AppointmentService apptServ;
	
	@GetMapping("/by-doctor")
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
	            apptServ.getAppointmentByDoctor(authentication.getName(), status, type, pageable);

	    return ApiResponse.<Page<AppointmentDTO>>builder()
	            .status(200)
	            .message("Lấy danh sách lịch hẹn thành công")
	            .data(result)
	            .build();
	}
	
	@PutMapping("/confirm/{appointmentId}")
	public ApiResponse<Void> confirmAppointment(Authentication authentication, @PathVariable Long appointmentId){
		
		Long doctorId = uServ.getIdFromEmail(authentication.getName());
		apptServ.confirmAppointmentDoctor(doctorId, appointmentId);
		
		return ApiResponse.<Void>builder()
				.status(200)
				.message("Cập nhật thành công")
				.build();
	}
	
	@PostMapping("/rebook")
	public ApiResponse<Void> rebookAppointment(Authentication authentication, @RequestBody AppointmentRequest req){
		
		if (authentication == null || !authentication.isAuthenticated()) {
		    return ApiResponse.<Void>builder()
		            .status(401)
		            .message("Chưa đăng nhập")
		            .build();
		}
		apptServ.rebookSlot(req);
		
		return ApiResponse.<Void>builder()
				.status(200)
				.message("Hủy thành công")
				.build();
	}
	
}
