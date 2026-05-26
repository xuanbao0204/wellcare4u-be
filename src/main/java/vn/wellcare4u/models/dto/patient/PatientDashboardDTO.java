package vn.wellcare4u.models.dto.patient;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.wellcare4u.models.dto.AppointmentDTO;
import vn.wellcare4u.models.dto.NotificationDTO;
import vn.wellcare4u.models.dto.PatientDTO;
import vn.wellcare4u.models.dto.doctor.VitalSignDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDashboardDTO {

	private PatientDTO profile;
	private AppointmentDTO upcomingAppointment;
	private List<AppointmentDTO> recentAppointments;
	private List<NotificationDTO> recentNotifications;
	private List<VitalSignDTO> vitalSignHistory;

	private MedicalSummaryDTO medicalSummary;
	private PatientDashboardStatsDTO stats;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MedicalSummaryDTO {
		private String aiSummary;
		private int totalRecords;
		private LocalDate lastVisitDate;
		private List<String> recentDiagnoses;
		private List<String> activeTreatments;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PatientDashboardStatsDTO {
		private long totalAppointments;
		private long completedAppointments;
		private long cancelledAppointments;
		private long pendingAppointments;
		private int totalMedicalRecords;
		private LocalDate lastVisitDate;
		private int unreadNotifications;
	}
}