package vn.wellcare4u.services.impl;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import vn.wellcare4u.entities.Patient;
import vn.wellcare4u.entities.patient.PatientDashboardSnapshot;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.models.dto.AppointmentDTO;
import vn.wellcare4u.models.dto.NotificationDTO;
import vn.wellcare4u.models.dto.PatientDTO;
import vn.wellcare4u.models.dto.UserDTO;
import vn.wellcare4u.models.dto.doctor.VitalSignDTO;
import vn.wellcare4u.models.dto.patient.PatientDashboardDTO;
import vn.wellcare4u.models.request.PatientProfileRequest;
import vn.wellcare4u.repositories.AppointmentRepository;
import vn.wellcare4u.repositories.NotificationRecipientRepository;
import vn.wellcare4u.repositories.PatientRepository;
import vn.wellcare4u.services.AppointmentService;
import vn.wellcare4u.services.NotificationService;
import vn.wellcare4u.services.PatientDashboardSnapshotService;
import vn.wellcare4u.services.PatientService;
import vn.wellcare4u.services.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {

	private final PatientRepository patientRepo;
	private final UserService uServ;
	private final AppointmentRepository appointmentRepo;
	private final NotificationRecipientRepository notifRecipientRepo;
	private final AppointmentService appointmentService; 
	private final NotificationService notiServ;
	
	private final PatientDashboardSnapshotService snapshotService;
	private final ObjectMapper objectMapper;

	@Override
	public PatientDTO getPatientProfile(String email) {
		Patient patient = patientRepo.findByAccount_Email(email)
				.orElseThrow(() -> new AppException("Patient not found", "PATIENT_NOT_FOUND", HttpStatus.NOT_FOUND));
		return mapToDTO(patient);
	}
	
	@Override
	public PatientDTO updatePatientProfile(String email, PatientProfileRequest req) {
		Patient patient = patientRepo.findByAccount_Email(email)
				.orElseThrow(() -> new AppException("Patient not found", "PATIENT_NOT_FOUND", HttpStatus.NOT_FOUND));
		
		if (req.getBloodType() != null) patient.setBloodType(req.getBloodType());
		if (req.getEmergencyContact() != null) patient.setEmergencyContact(req.getEmergencyContact());
		if (req.getInsuranceImage() != null) patient.setInsuranceImage(req.getInsuranceImage());
		if (req.getInsuranceNumber() != null) patient.setInsuranceNumber(req.getInsuranceNumber());
		
		patientRepo.save(patient);
		return mapToDTO(patient);
	}
	
	private PatientDTO mapToFullDTO(Patient p) {
		
		UserDTO u = uServ.getUserInfoByEmail(p.getAccount().getEmail());
		return PatientDTO.builder()
				.id(p.getId())
				.avatar(u.getAvatar())
				.email(u.getEmail())
				.firstName(p.getFirstName())
				.lastName(p.getLastName())
				.gender(p.getGender())
				
				.emergencyContact(p.getEmergencyContact())
				.bloodType(p.getBloodType())
				.insuranceNumber(p.getInsuranceNumber())
				.insuranceImage(p.getInsuranceImage())
				.build();
	}
	
	private PatientDTO mapToDTO(Patient p) {
		return PatientDTO.builder()
				.emergencyContact(p.getEmergencyContact())
				.bloodType(p.getBloodType())
				.insuranceNumber(p.getInsuranceNumber())
				.insuranceImage(p.getInsuranceImage())
				.build();
	}
	
	@Override
	public PatientDashboardDTO getDashboard(String email) {

	    try {

	        Patient patient = patientRepo.findByAccount_Email(email)
	                .orElseThrow(() -> new AppException(
	                        "Patient not found",
	                        "PATIENT_NOT_FOUND",
	                        HttpStatus.NOT_FOUND
	                ));
	        
	        log.info("Patient email" +patient.getAccount().getEmail());

	        Long patientId = patient.getId();
	        
	        log.info("Patient id" +patient.getId());

	        PatientDashboardSnapshot snapshot =
	                snapshotService.getSnapshot(patientId);

	        PatientDTO profile = mapToFullDTO(patient);

	        AppointmentDTO upcomingAppointment =
	                appointmentRepo.findUpcomingByPatientId(patientId)
	                        .map(appointmentService::mapToDTO)
	                        .orElse(null);

	        List<NotificationDTO> recentNotifications =
	                notifRecipientRepo
	                        .findTop5ByUserIdOrderByCreatedAtDesc(
	                                patient.getAccount().getUser().getId(),
	                                PageRequest.of(0, 5)
	                        )
	                        .stream()
	                        .map(notiServ::mapToDTO)
	                        .toList();

	        int unreadNotifications =
	                (int) notifRecipientRepo.countUnread(
	                        patient.getAccount().getUser().getId()
	                );

	        List<String> recentDiagnoses =
	                objectMapper.readValue(
	                        snapshot.getRecentDiagnosesJson(),
	                        new TypeReference<>() {}
	                );

	        List<AppointmentDTO> recentAppointments =
	                objectMapper.readValue(
	                        snapshot.getRecentAppointmentsJson(),
	                        new TypeReference<>() {}
	                );

	        List<VitalSignDTO> vitalSigns =
	                objectMapper.readValue(
	                        snapshot.getVitalSignsJson(),
	                        new TypeReference<>() {}
	                );

	        PatientDashboardDTO.MedicalSummaryDTO medicalSummary =
	                PatientDashboardDTO.MedicalSummaryDTO.builder()
	                        .aiSummary(snapshot.getAiSummary())
	                        .totalRecords(snapshot.getTotalMedicalRecords())
	                        .lastVisitDate(snapshot.getLastVisitDate())
	                        .recentDiagnoses(recentDiagnoses)
	                        .build();

	        PatientDashboardDTO.PatientDashboardStatsDTO stats =
	                PatientDashboardDTO.PatientDashboardStatsDTO.builder()
	                        .totalAppointments(snapshot.getTotalAppointments())
	                        .completedAppointments(snapshot.getCompletedAppointments())
	                        .cancelledAppointments(snapshot.getCancelledAppointments())
	                        .pendingAppointments(snapshot.getPendingAppointments())
	                        .totalMedicalRecords(snapshot.getTotalMedicalRecords())
	                        .lastVisitDate(snapshot.getLastVisitDate())
	                        .unreadNotifications(unreadNotifications)
	                        .build();

	        return PatientDashboardDTO.builder()
	                .profile(profile)
	                .upcomingAppointment(upcomingAppointment)
	                .recentAppointments(recentAppointments)
	                .recentNotifications(recentNotifications)
	                .vitalSignHistory(vitalSigns)
	                .medicalSummary(medicalSummary)
	                .stats(stats)
	                .build();

	    } catch (Exception ex) {
	        throw new RuntimeException(ex);
	    }
	}
	
}
