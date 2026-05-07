package vn.wellcare4u.models.dto.doctor;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class PatientSummaryDTO {
    private Long patientId;
    private String firstName;
    private String lastName;
    private String avatar;
    private String gender;
    private LocalDate dob;
    private int totalRecords;
    private LocalDate lastVisitDate;
}