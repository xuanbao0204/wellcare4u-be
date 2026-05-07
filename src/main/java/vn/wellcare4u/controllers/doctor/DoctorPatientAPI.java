package vn.wellcare4u.controllers.doctor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.dto.doctor.PatientMedicalRecordsDTO;
import vn.wellcare4u.models.dto.doctor.PatientSummaryDTO;
import vn.wellcare4u.services.MedicalRecordService;
import vn.wellcare4u.services.UserService;

@RestController
@RequestMapping("/api/v1/doctor")
public class DoctorPatientAPI {

	@Autowired
	private MedicalRecordService medicalRecordServ;
	
	@Autowired
	private UserService uServ;
	
	@GetMapping("/patients-manage")
	@PreAuthorize("hasRole('DOCTOR')")
	public ApiResponse<List<PatientSummaryDTO>> getPatientsWithRecords(Authentication auth) {
		
		if (auth == null || !auth.isAuthenticated()) {
		    return ApiResponse.<List<PatientSummaryDTO>>builder()
		            .status(401)
		            .message("Chưa đăng nhập")
		            .build();
		}
		
		Long doctorId = uServ.getIdFromEmail(auth.getName());
		
	    return ApiResponse.<List<PatientSummaryDTO>>builder()
	    		.status(200)
	    		.message("Get successfully")
	    		.data(medicalRecordServ.getPatientsSummaryByDoctor(doctorId))
	    		.build();
	}
	
	@GetMapping("/patients-manage/{patientId}")
	@PreAuthorize("hasRole('DOCTOR')")
	public ApiResponse<PatientMedicalRecordsDTO> getPatientsWithRecords(Authentication auth, @PathVariable Long patientId) {
		
		if (auth == null || !auth.isAuthenticated()) {
		    return ApiResponse.<PatientMedicalRecordsDTO>builder()
		            .status(401)
		            .message("Chưa đăng nhập")
		            .build();
		}
		
		Long doctorId = uServ.getIdFromEmail(auth.getName());
		
	    return ApiResponse.<PatientMedicalRecordsDTO>builder()
	    		.status(200)
	    		.message("Get successfully")
	    		.data(medicalRecordServ.getPatientDetails(doctorId, patientId))
	    		.build();
	}
}
