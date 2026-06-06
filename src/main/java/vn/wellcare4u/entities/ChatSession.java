package vn.wellcare4u.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.wellcare4u.enums.EAppointmentType;
import vn.wellcare4u.enums.ESpecialization;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_session", indexes = { @Index(name = "idx_chat_patient", columnList = "patientId", unique = true),
		@Index(name = "idx_chat_updated", columnList = "updatedAt") })
public class ChatSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private Long patientId;

	@Enumerated(EnumType.STRING)
	private ESpecialization specialization;

	@Enumerated(EnumType.STRING)
	private ESpecialization pendingSpecialization;

	private LocalDate appointmentDate;


	@Enumerated(EnumType.STRING)
	private EAppointmentType appointmentType;

	@Column(length = 1000)
	private String reason;

	@Lob
	private String slotCacheJson;

	private LocalDateTime updatedAt;
}