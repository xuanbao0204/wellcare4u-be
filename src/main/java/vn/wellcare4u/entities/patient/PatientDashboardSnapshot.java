package vn.wellcare4u.entities.patient;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "patient_dashboard_snapshots")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDashboardSnapshot {

    @Id
    private Long patientId;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String aiSummary;

    private Integer totalMedicalRecords;

    private LocalDate lastVisitDate;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String recentDiagnosesJson;

    private Long totalAppointments;

    private Long completedAppointments;

    private Long cancelledAppointments;

    private Long pendingAppointments;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String recentAppointmentsJson;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String vitalSignsJson;

    private Instant refreshedAt;

    private Long version;
}