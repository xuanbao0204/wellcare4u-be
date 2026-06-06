package vn.wellcare4u.services.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;
import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.entities.doctor.DoctorDashboardSnapshot;
import vn.wellcare4u.entities.medical.MedicalRecord;
import vn.wellcare4u.enums.EAppointmentStatus;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.models.dto.AppointmentDTO;
import vn.wellcare4u.repositories.AppointmentRepository;
import vn.wellcare4u.repositories.DoctorRepository;
import vn.wellcare4u.repositories.doctor.DoctorDashboardSnapshotRepository;
import vn.wellcare4u.repositories.medical.MedicalRecordRepository;
import vn.wellcare4u.services.AIToolService;
import vn.wellcare4u.services.AppointmentService;
import vn.wellcare4u.services.DoctorDashboardSnapshotService;

@Service
@RequiredArgsConstructor
public class DoctorDashboardSnapshotServiceImpl implements DoctorDashboardSnapshotService {

    private final AppointmentRepository appointmentRepo;
    private final MedicalRecordRepository medicalRecordRepo;
    private final DoctorDashboardSnapshotRepository snapshotRepo;
    private final DoctorRepository doctorRepo;
    private final AppointmentService appointmentService;
    private final ObjectMapper objectMapper;
    private final AIToolService aiTools;

    @Override
    public DoctorDashboardSnapshot getSnapshot(Long doctorId) {
        return snapshotRepo.findById(doctorId)
                .orElseGet(() -> {
                    rebuildSnapshot(doctorId);
                    return snapshotRepo.findById(doctorId).orElseThrow();
                });
    }
    
    @Override
	public String generateSummary(Long doctorId) {
    	 Doctor doctor = doctorRepo.findById(doctorId)
                 .orElseThrow(() -> new AppException(
                         "Doctor not found",
                         "DOCTOR_NOT_FOUND",
                         HttpStatus.NOT_FOUND
                 ));
    	List<Appointment> allAppointments = appointmentRepo.findAllByDoctorId(doctorId);
        List<MedicalRecord> records       = medicalRecordRepo.findByDoctorId(doctorId);
        
        String aiSummary = aiTools.summarizeDoctorDashboard(doctor, allAppointments, records);

        DoctorDashboardSnapshot snapshot = snapshotRepo.findById(doctorId)
                .orElse(DoctorDashboardSnapshot.builder()
                        .doctorId(doctorId)
                        .build());
        snapshot.setAiSummary(aiSummary);
        snapshotRepo.save(snapshot);
        return aiSummary;

    }
    
    @Override
    @Transactional
    public void rebuildSnapshot(Long doctorId) {

        long totalAppointments    = appointmentRepo.countByDoctorId(doctorId);
        long completedAppointments = appointmentRepo.countByDoctorIdAndStatus(doctorId, EAppointmentStatus.COMPLETED);
        long cancelledAppointments = appointmentRepo.countByDoctorIdAndStatus(doctorId, EAppointmentStatus.CANCELLED);
        long totalPatients         = appointmentRepo.countDistinctPatientsByDoctorId(doctorId);
        long totalMedicalRecords   = medicalRecordRepo.countByDoctorId(doctorId);

        double cancellationRate = totalAppointments == 0
                ? 0
                : (cancelledAppointments * 100.0) / totalAppointments;

        List<AppointmentDTO> recentAppointments =
                appointmentRepo.findRecentAppointments(doctorId, PageRequest.of(0, 5))
                        .stream()
                        .map(appointmentService::mapToDTO)
                        .toList();

        DoctorDashboardSnapshot snapshot = snapshotRepo.findById(doctorId)
                .orElse(DoctorDashboardSnapshot.builder()
                        .doctorId(doctorId)
                        .build());

        try {
            snapshot.setTotalAppointments(totalAppointments);
            snapshot.setCompletedAppointments(completedAppointments);
            snapshot.setCancelledAppointments(cancelledAppointments);
            snapshot.setCancellationRate(cancellationRate);
            snapshot.setTotalPatients(totalPatients);
            snapshot.setTotalMedicalRecords(totalMedicalRecords);
            snapshot.setRecentAppointmentsJson(objectMapper.writeValueAsString(recentAppointments));
            snapshot.setRefreshedAt(Instant.now());
            snapshot.setAiSummary(null);

            snapshotRepo.save(snapshot);

        } catch (Exception ex) {
            throw new RuntimeException("Failed to serialize snapshot data", ex);
        }
    }
}