package vn.wellcare4u.entities.doctor;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doctor_dashboard_snapshot")
public class DoctorDashboardSnapshot {

    @Id
    private Long doctorId;

    private Long totalAppointments;
    private Long completedAppointments;
    private Long cancelledAppointments;
    private Double cancellationRate;
    private Long totalPatients;
    private Long totalMedicalRecords;
    @Lob
    private String aiSummary;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String recentAppointmentsJson;

    private Instant refreshedAt;
}