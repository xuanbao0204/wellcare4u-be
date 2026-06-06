package vn.wellcare4u.controllers.doctor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.wellcare4u.entities.User;
import vn.wellcare4u.enums.ENotificationTarget;
import vn.wellcare4u.enums.ENotificationType;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.request.NotificationRequest;
import vn.wellcare4u.repositories.UserRepository;
import vn.wellcare4u.services.NotificationService;
import vn.wellcare4u.services.PatientService;

@RestController
@RequestMapping("/api/v1/doctor/notifications")
@RequiredArgsConstructor
public class DoctorNotificationAPI {

	private final NotificationService notificationService;
	private final PatientService patientService;
	private final UserRepository userRepo;

	@PostMapping("/send")
	@PreAuthorize("hasRole('DOCTOR')")
	public ApiResponse<Void> send(@RequestBody @Valid NotificationRequest req, Authentication auth) {

		if (req.getTarget() != ENotificationTarget.IDS) {
			throw new AppException("Doctor chỉ được gửi cho bệnh nhân cụ thể", "FORBIDDEN_TARGET",
					HttpStatus.FORBIDDEN);
		}

		User doctor = userRepo.findByAccount_Email(auth.getName())
				.orElseThrow(() -> new AppException("Not found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));

		List<Long> allowedIds = new ArrayList<>(patientService.getPatientIdsByDoctor(doctor.getId()).keySet());

		boolean hasUnauthorized = req.getReceiverIds().stream().anyMatch(id -> !allowedIds.contains(id));

		if (hasUnauthorized) {
			throw new AppException("Không thể gửi cho bệnh nhân không thuộc danh sách của bạn", "FORBIDDEN_RECIPIENT",
					HttpStatus.FORBIDDEN);
		}

		req.setType(ENotificationType.INFO);

		notificationService.send(req, auth.getName());

		return ApiResponse.<Void>builder().status(HttpStatus.OK.value()).message("Gửi thông báo thành công").build();
	}

	@GetMapping("/my-patients")
	@PreAuthorize("hasRole('DOCTOR')")
	public ApiResponse<Map<Long, String>> getMyPatients(Authentication auth) {
		User doctor = userRepo.findByAccount_Email(auth.getName())
				.orElseThrow(() -> new AppException("Not found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));

		return ApiResponse.<Map<Long, String>>builder().status(200).message("Success")
				.data(patientService.getPatientIdsByDoctor(doctor.getId())).build();
	}
}