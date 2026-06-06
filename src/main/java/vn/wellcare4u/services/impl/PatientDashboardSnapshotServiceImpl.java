package vn.wellcare4u.services.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;
import vn.wellcare4u.entities.Patient;
import vn.wellcare4u.entities.medical.MedicalRecord;
import vn.wellcare4u.entities.medical.VitalSign;
import vn.wellcare4u.entities.patient.PatientDashboardSnapshot;
import vn.wellcare4u.enums.EAppointmentStatus;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.models.dto.AppointmentDTO;
import vn.wellcare4u.models.dto.doctor.VitalSignDTO;
import vn.wellcare4u.repositories.AppointmentRepository;
import vn.wellcare4u.repositories.PatientRepository;
import vn.wellcare4u.repositories.medical.MedicalRecordRepository;
import vn.wellcare4u.repositories.medical.VitalSignRepository;
import vn.wellcare4u.repositories.patient.PatientDashboardSnapshotRepository;
import vn.wellcare4u.services.AIToolService;
import vn.wellcare4u.services.AppointmentService;
import vn.wellcare4u.services.PatientDashboardSnapshotService;

@Service
@RequiredArgsConstructor
public class PatientDashboardSnapshotServiceImpl implements PatientDashboardSnapshotService {

    private final PatientRepository patientRepo;
    private final AppointmentRepository appointmentRepo;
    private final MedicalRecordRepository recordRepo;
    private final VitalSignRepository vitalSignRepo;
    private final PatientDashboardSnapshotRepository snapshotRepo;
    private final AIToolService aiTools;

    private final AppointmentService appointmentService;

    private final ObjectMapper objectMapper;

    @Override
    public PatientDashboardSnapshot getSnapshot(Long patientId) {
        return snapshotRepo.findById(patientId)
                .orElseGet(() -> {
                    rebuildSnapshot(patientId);
                    return snapshotRepo.findById(patientId)
                            .orElseThrow();
                });
    }

    @Override
	public String generateSummary(Long patientId) {
    	 Patient patient = patientRepo.findById(patientId)
                 .orElseThrow(() -> new AppException(
                         "Patient not found",
                         "PATIENT_NOT_FOUND",
                         HttpStatus.NOT_FOUND
                 ));
    	 
		List<MedicalRecord> records = recordRepo.findByPatientIdOrderByCreatedAtDesc(patientId);
        
        String aiSummary = aiTools.summarizeMedicalHistory(records, patient);

        PatientDashboardSnapshot snapshot = snapshotRepo.findById(patientId)
                .orElse(PatientDashboardSnapshot.builder()
                        .patientId(patientId)
                        .build());
        snapshot.setAiSummary(aiSummary);
        snapshotRepo.save(snapshot);
        return aiSummary;

    }
    
    @Override
    @Transactional
    public void rebuildSnapshot(Long patientId) {
        List<MedicalRecord> records =
                recordRepo.findByPatientIdOrderByCreatedAtDesc(patientId);

        LocalDate lastVisitDate = records.isEmpty()
                ? null
                : records.get(0).getCreatedAt().toLocalDate();

        List<String> recentDiagnoses = records.stream()
                .map(MedicalRecord::getDiagnosis)
                .filter(Objects::nonNull)
                .distinct()
                .limit(5)
                .toList();

        List<AppointmentDTO> recentAppointments =
                appointmentRepo.findTop3RecentByPatientId(patientId)
                        .stream()
                        .map(appointmentService::mapToDTO)
                        .toList();

        List<VitalSignDTO> vitalSigns =
                vitalSignRepo.findTop5ByPatientIdOrderByTimestampDesc(patientId)
                        .stream()
                        .map(this::mapToVitalSignDTO)
                        .toList();

        long totalAppointments =
                appointmentRepo.countByPatientId(patientId);

        long completedAppointments =
                appointmentRepo.countByPatientIdAndStatus(
                        patientId,
                        EAppointmentStatus.COMPLETED
                );

        long cancelledAppointments =
                appointmentRepo.countByPatientIdAndStatus(
                        patientId,
                        EAppointmentStatus.CANCELLED
                );

        long pendingAppointments =
                appointmentRepo.countByPatientIdAndStatus(
                        patientId,
                        EAppointmentStatus.PENDING
                );
        PatientDashboardSnapshot snapshot =
                snapshotRepo.findById(patientId)
                        .orElse(
                                PatientDashboardSnapshot.builder()
                                        .patientId(patientId)
                                        .version(0L)
                                        .build()
                        );

        try {

            snapshot.setAiSummary(null);

            snapshot.setTotalMedicalRecords(records.size());

            snapshot.setLastVisitDate(lastVisitDate);

            snapshot.setRecentDiagnosesJson(
                    objectMapper.writeValueAsString(recentDiagnoses)
            );

            snapshot.setRecentAppointmentsJson(
                    objectMapper.writeValueAsString(recentAppointments)
            );

            snapshot.setVitalSignsJson(
                    objectMapper.writeValueAsString(vitalSigns)
            );

            snapshot.setTotalAppointments(totalAppointments);

            snapshot.setCompletedAppointments(completedAppointments);

            snapshot.setCancelledAppointments(cancelledAppointments);

            snapshot.setPendingAppointments(pendingAppointments);

            snapshot.setRefreshedAt(Instant.now());

            snapshot.setVersion(snapshot.getVersion() + 1);

            snapshotRepo.save(snapshot);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private VitalSignDTO mapToVitalSignDTO(VitalSign v) {
        return VitalSignDTO.builder()
                .height(v.getHeight())
                .weight(v.getWeight())
                .bmi(v.getBmi())
                .bloodPressure(v.getBloodPressure())
                .heartRate(v.getHeartRate())
                .bloodSugar(v.getBloodSugar())
                .build();
    }
}