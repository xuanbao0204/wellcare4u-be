package vn.wellcare4u.models.dto.doctor;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.wellcare4u.models.dto.AppointmentDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDashboardSnapshotDTO {

    private DoctorDTO profile;

    private DoctorDashboardStatsDTO stats;
    private List<AppointmentDTO> upcomingAppointments;

    private List<AppointmentDTO> recentAppointments;

    private String aiSummary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorDashboardStatsDTO {

        private Long totalAppointments;
        private Long completedAppointments;
        private Long cancelledAppointments;
        private Double cancellationRate;
        private Long totalPatients;
        private Long totalMedicalRecords;
        private Long todayAppointments;
    }
}