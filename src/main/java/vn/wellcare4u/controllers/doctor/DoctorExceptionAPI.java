package vn.wellcare4u.controllers.doctor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.dto.AppointmentDTO;
import vn.wellcare4u.models.dto.doctor.DoctorExceptionDTO;
import vn.wellcare4u.models.request.CreateExceptionRequest;
import vn.wellcare4u.services.DoctorExceptionService;
import vn.wellcare4u.services.UserService;

@RestController
@RequestMapping("/api/v1/doctor/exceptions")
@RequiredArgsConstructor
public class DoctorExceptionAPI {

    private final DoctorExceptionService exceptionService;
    
    @Autowired
	private UserService uServ;
    
    @PostMapping
    public ApiResponse<DoctorExceptionDTO> createDayOff(
            Authentication authentication,
            @RequestBody @Valid CreateExceptionRequest req) {
    	if (authentication == null || !authentication.isAuthenticated()) {
		    return ApiResponse.<DoctorExceptionDTO>builder()
		            .status(401)
		            .message("Chưa đăng nhập")
		            .build();
		}
    	Long doctorId = uServ.getIdFromEmail(authentication.getName());
        req.setDoctorId(doctorId);
        return ApiResponse.<DoctorExceptionDTO>builder()
            .status(HttpStatus.CREATED.value())
            .data(exceptionService.createDayOff(doctorId, req))
            .build();
    }

    @DeleteMapping
    public ApiResponse<Void> revokeDayOff(
    		Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

    	if (authentication == null || !authentication.isAuthenticated()) {
		    return ApiResponse.<Void>builder()
		            .status(401)
		            .message("Chưa đăng nhập")
		            .build();
		}
    	Long doctorId = uServ.getIdFromEmail(authentication.getName());
        exceptionService.revokeDayOff(doctorId, date);
        return ApiResponse.<Void>builder()
        		.build();
    }

    @GetMapping
    public ApiResponse<List<DoctorExceptionDTO>> getDayOffs(
    		Authentication authentication) {
    	if (authentication == null || !authentication.isAuthenticated()) {
		    return ApiResponse.<List<DoctorExceptionDTO>>builder()
		            .status(401)
		            .message("Chưa đăng nhập")
		            .build();
		}
    	Long doctorId = uServ.getIdFromEmail(authentication.getName());
        return ApiResponse.<List<DoctorExceptionDTO>>builder()
	            .status(200)
	            .message("OK")
	            .data(exceptionService.getDayOffsByDoctor(doctorId))
	            .build();
    }
}