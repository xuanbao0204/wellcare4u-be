package vn.wellcare4u.entities.doctor;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.wellcare4u.enums.ETimeSlotStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "time_slot", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "doctor_id", "date", "startTime" }) })
public class TimeSlot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "doctor_id")
	@JsonIgnoreProperties({ "timeSlots", "schedules" })
	private Doctor doctor;

	@ManyToOne
	@JoinColumn(name = "schedule_id")
	@JsonIgnore
	private DoctorSchedule schedule;

	private LocalDate date;

	private LocalTime startTime;
	private LocalTime endTime;

	@Enumerated(EnumType.STRING)
	private ETimeSlotStatus status;

	@Version
	private Long version;
}