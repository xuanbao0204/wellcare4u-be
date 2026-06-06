package vn.wellcare4u.entities.medical;

import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.entities.Patient;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.enums.ERecordStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "medical_record")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Appointment appointment;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Doctor doctor;

    private String chiefComplaint;
    private String symptoms;

    @Column(length = 2000)
    private String diagnosis;

    private String icdCode;

    @Column(length = 2000)
    private String treatmentPlan;

    @Column(length = 2000)
    private String conclusion;

//    private LocalDate followUpDate;
    @OneToOne
    @Nullable
    private Appointment followUpDate;

    @Enumerated(EnumType.STRING)
    private ERecordStatus status;

    private LocalDateTime createdAt;
    
    public String infoForSummary() {
        return String.format("""
                RecordID: %s
                Symptoms: %s
                Diagnosis: %s
                ICDCode: %s
                TreatmentPlan: %s
                Conclusion: %s
                FollowUp: %s
                Status: %s
                CreatedAt: %s
                """,
                id,
                symptoms,
                diagnosis,
                icdCode,
                treatmentPlan,
                conclusion,
                followUpDate != null ? followUpDate.getId() : "None",
                status,
                createdAt
        );
    }
}