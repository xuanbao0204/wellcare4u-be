package vn.wellcare4u.controllers.medical_record;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.dto.AppointmentDTO;
import vn.wellcare4u.models.request.medical.CreateRecordRequest;
import vn.wellcare4u.services.MedicalRecordService;
import vn.wellcare4u.services.UserService;

@RestController
@RequestMapping("/api/v1/medical-records")
public class MedicalRecordAPI {

	@Autowired
	private MedicalRecordService medicalRecordServ;
	
	@Autowired
	private UserService uServ;
	
	@PostMapping("/create/{appointmentId}")
	public ApiResponse<?> createRecord(@PathVariable Long appointmentId, Authentication auth) {
		
		if (auth == null || !auth.isAuthenticated()) {
		    return ApiResponse.<Page<AppointmentDTO>>builder()
		            .status(401)
		            .message("Chưa đăng nhập")
		            .build();
		}
		
		Long doctorId = uServ.getIdFromEmail(auth.getName());
		
	    Long recordId = medicalRecordServ.startExam(appointmentId, doctorId);
	    return ApiResponse.builder()
	            .status(HttpStatus.OK.value())
	            .message("Tạo phiếu khám thành công")
	            .data(recordId)
	            .build();
	}
	
	@PostMapping("/finalize")
	public ApiResponse<?> finalize(@RequestBody CreateRecordRequest req, Authentication auth) {
		if (auth == null || !auth.isAuthenticated()) {
		    return ApiResponse.<Page<AppointmentDTO>>builder()
		            .status(401)
		            .message("Chưa đăng nhập")
		            .build();
		}
		
		Long doctorId = uServ.getIdFromEmail(auth.getName());
	    medicalRecordServ.finalizeRecord(req, doctorId);
	    return ApiResponse.builder()
	            .status(HttpStatus.OK.value())
	            .message("Kết thúc phiếu khám thành công")
	            .build();
	}
	
	@GetMapping("/patient/{pid}")
	public ApiResponse<?> getRecordByPatient(@PathVariable Long pid) {
	    return ApiResponse.builder()
	            .status(HttpStatus.OK.value())
	            .message("Lấy phiếu khám thành công")
	            .data(medicalRecordServ.getRecordsByPatient(pid))
	            .build();
	}
	
	@GetMapping("/doctor/{did}")
	public ApiResponse<?> getRecordByDoctor(@PathVariable Long did) {
	    return ApiResponse.builder()
	            .status(HttpStatus.OK.value())
	            .message("Lấy phiếu khám thành công")
	            .data(medicalRecordServ.getRecordsByDoctor(did))
	            .build();
	}
	
	@GetMapping("/detail/{recordId}")
	public ApiResponse<?> getRecordDetail(@PathVariable Long recordId) {
	    return ApiResponse.builder()
	            .status(HttpStatus.OK.value())
	            .message("Lấy chi tiết phiếu khám thành công")
	            .data(medicalRecordServ.getRecordDetail(recordId))
	            .build();
	}
	
	@GetMapping("/detail/print/{recordId}")
	public ApiResponse<?> getRecordDetailPrint(@PathVariable Long recordId) {
	    return ApiResponse.builder()
	            .status(HttpStatus.OK.value())
	            .message("Lấy chi tiết phiếu khám thành công")
	            .data(medicalRecordServ.getRecordDetailPrint(recordId))
	            .build();
	}
}
