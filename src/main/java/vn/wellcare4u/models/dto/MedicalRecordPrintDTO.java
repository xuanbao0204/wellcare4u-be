package vn.wellcare4u.models.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.wellcare4u.enums.EAppointmentStatus;
import vn.wellcare4u.enums.EAppointmentType;
import vn.wellcare4u.models.dto.doctor.DoctorDTO;
import vn.wellcare4u.models.dto.doctor.VitalSignDTO;

@Data
@Builder
public class MedicalRecordPrintDTO {

    private Long recordId;
    private DoctorDTO doctor;
    private PatientDTO patient;

    private String chiefComplaint;
    private String symptoms;

    private String diagnosis;
    private String icdCode;

    private String treatmentPlan;
    private String conclusion;

    private LocalDate followUpDate;

    private VitalSignDTO vitalSign;

    private List<MedicalTestDTO> tests;

    private List<PrescriptionItemDTO> items;
    private LocalDateTime createdAt;
    
    private AppointmentInfo appointment;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppointmentInfo {
    	private Long slotId;
        private String slotTime;
        private LocalDate slotDate;

        private String reason;
        private EAppointmentType type;
        private EAppointmentStatus status;
    }
}
