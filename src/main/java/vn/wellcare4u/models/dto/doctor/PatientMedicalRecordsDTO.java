package vn.wellcare4u.models.dto.doctor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientMedicalRecordsDTO {

    private Long patientId;
    private String firstName;
    private String lastName;
    private String avatar;
    private String gender;
    private LocalDate dob;

    private int totalRecords;
    private LocalDate lastVisitDate;
    private List<VitalSignDTO> vitalSigns;
    private List<RecordSummary> records;

    @Data
    @Builder
    public static class RecordSummary {
        private Long recordId;
        private String diagnosis;
        private LocalDateTime createdAt;
    }
}