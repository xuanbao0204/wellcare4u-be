package vn.wellcare4u.controllers.ai;

import lombok.RequiredArgsConstructor;
import vn.wellcare4u.mapper.JsonParser;
import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.dto.patient.SuggestSpecializationDTO;
import vn.wellcare4u.models.request.SuggestSpecializationRequest;
import vn.wellcare4u.services.AIToolService;
import vn.wellcare4u.services.DoctorDashboardSnapshotService;
import vn.wellcare4u.services.PatientDashboardSnapshotService;
import vn.wellcare4u.services.UserService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai-tools")
@RequiredArgsConstructor
public class AIToolController {

	private final UserService uServ;
	private final DoctorDashboardSnapshotService doctorDashboardServ;
	private final PatientDashboardSnapshotService patientDashboardServ;
	private final AIToolService aiTools;

	@GetMapping("/doctor-summary")
	@PreAuthorize("hasRole('DOCTOR')")
	public ApiResponse<String> getDoctorDashboardSummary(Authentication auth) {
		return ApiResponse.<String>builder()
				.status(200)
				.message("Get successfully")
				.data(doctorDashboardServ.generateSummary(uServ.getIdFromEmail(auth.getName())))
				.build();
	}
	
	@GetMapping("/patient-summary")
	@PreAuthorize("hasRole('PATIENT')")
	public ApiResponse<String> getPatientDashboardSummary(Authentication auth) {
		return ApiResponse.<String>builder()
				.status(200)
				.message("Get successfully")
				.data(patientDashboardServ.generateSummary(uServ.getIdFromEmail(auth.getName())))
				.build();
	}
	
	@PostMapping("/suggest-specialization")
	public ApiResponse<SuggestSpecializationDTO> getSpecializationSuggestion(Authentication auth, @RequestBody SuggestSpecializationRequest req) {
		return ApiResponse.<SuggestSpecializationDTO>builder()
				.status(200)
				.message("Get successfully")
				.data(JsonParser.parse(aiTools.getSuggestionSpecialization(req), SuggestSpecializationDTO.class))
				.build();
	}
}