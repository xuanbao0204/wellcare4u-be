package vn.wellcare4u.services.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.entities.Patient;
import vn.wellcare4u.entities.doctor.TimeSlot;
import vn.wellcare4u.entities.medical.MedicalRecord;
import vn.wellcare4u.enums.EAppointmentEventType;
import vn.wellcare4u.enums.EAppointmentStatus;
import vn.wellcare4u.enums.EAppointmentType;
import vn.wellcare4u.enums.ECancelBy;
import vn.wellcare4u.enums.ETimeSlotStatus;
import vn.wellcare4u.events.AppointmentEvent;
import vn.wellcare4u.events.DoctorDashboardChangedEvent;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.models.dto.AppointmentDTO;
import vn.wellcare4u.models.request.AppointmentRequest;
import vn.wellcare4u.models.request.CancelAppointmentRequest;
import vn.wellcare4u.repositories.AppointmentRepository;
import vn.wellcare4u.repositories.PatientRepository;
import vn.wellcare4u.repositories.doctor.TimeSlotRepository;
import vn.wellcare4u.repositories.medical.MedicalRecordRepository;
import vn.wellcare4u.services.AppointmentService;
import vn.wellcare4u.services.UserService;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

	@Autowired
	private TimeSlotRepository timeSlotRepo;
	@Autowired
	private AppointmentRepository appointmentRepo;

	@Autowired
	private PatientRepository patientRepo;

	@Autowired
	private UserService uServ;

	@Autowired
	private MedicalRecordRepository recordRepo;
	
	private final ApplicationEventPublisher publisher;

	@Override
	public List<AppointmentDTO> getAppointmentByPatient(Long patientId) {

		List<Appointment> result = appointmentRepo.findByPatientId(patientId);

		return result.stream().map(this::mapToDTO).toList();
	}

	@Override
	public Page<AppointmentDTO> getAppointmentByPatient(String patientEmail, EAppointmentStatus status,
			EAppointmentType type, Pageable pageable) {
		Long patientId = uServ.getIdFromEmail(patientEmail);

		Page<Appointment> page = appointmentRepo.findByPatientIdAndFilters(patientId, status, type, pageable);
		return page.map(this::mapToDTO);
	}

	@Override
	public Page<AppointmentDTO> getAppointmentByDoctor(String doctorEmail, EAppointmentStatus status,
			EAppointmentType type, Pageable pageable) {
		Long doctorId = uServ.getIdFromEmail(doctorEmail);

		Page<Appointment> page = appointmentRepo.findByDoctorIdAndFilters(doctorId, status, type, pageable);
		return page.map(this::mapToDTO);
	}

	@Override
	@Transactional
	public void cancel(Long userId, Long appointmentId, CancelAppointmentRequest req) {

		Appointment apt = appointmentRepo.findById(appointmentId).orElseThrow(
				() -> new AppException("Appointment không hợp lệ", "APPOINTMENT_NOT_FOUND", HttpStatus.BAD_REQUEST));
		boolean isPatient = userId.equals(apt.getPatient().getId());
		boolean isDoctor = userId.equals(apt.getDoctor().getId());

		if (!isPatient && !isDoctor) {
			throw new AppException("Không có quyền", "FORBIDDEN", HttpStatus.FORBIDDEN);
		}

		if (apt.getStatus() == EAppointmentStatus.COMPLETED || apt.getStatus() == EAppointmentStatus.CANCELLED) {
			throw new AppException("Không thể hủy", "INVALID_STATE", HttpStatus.BAD_REQUEST);
		}

		LocalDateTime appointmentTime = apt.getTimeSlot().getDate().atTime(apt.getTimeSlot().getStartTime());

		LocalDateTime now = LocalDateTime.now();

		if (isPatient) {
			if (now.isAfter(appointmentTime.minusHours(24))) {
				throw new AppException("Đã quá hạn hủy", "CANCEL_TIME_EXPIRED", HttpStatus.BAD_REQUEST);
			}
		}

		apt.setStatus(EAppointmentStatus.CANCELLED);
		apt.setCancelBy(isDoctor ? ECancelBy.DOCTOR : ECancelBy.PATIENT);
		apt.setCancelReason(req.getReason());
		apt.setCancelledAt(now);
		apt.setUpdatedAt(now);

		appointmentRepo.save(apt);

		TimeSlot slot = apt.getTimeSlot();

		if (isPatient) {
			slot.setStatus(ETimeSlotStatus.AVAILABLE);
		} else {
			slot.setStatus(ETimeSlotStatus.AVAILABLE);
		}

		timeSlotRepo.save(slot);
		
		publisher.publishEvent(
		        new AppointmentEvent(
		                EAppointmentEventType.CANCELLED,
		                apt,
		                isPatient ? "PATIENT" : "DOCTOR",
		                req.getReason()
		        )
		);
		publisher.publishEvent(
                new DoctorDashboardChangedEvent(
                        apt.getDoctor().getId()
                )
        );
		
	}


	@Override
	@Transactional
	public AppointmentDTO bookSlot(AppointmentRequest req, Long patientId) {

		Patient patient = patientRepo.findById(patientId).orElseThrow(
				() -> new AppException("Không tìm thấy bệnh nhân", "PATIENT_NOT_FOUND", HttpStatus.BAD_REQUEST));

		TimeSlot slot = timeSlotRepo.findLockedById(req.getSlotId())
				.orElseThrow(() -> new RuntimeException("Slot không tồn tại."));

		if (slot.getStatus() != ETimeSlotStatus.AVAILABLE) {

			throw new RuntimeException("Slot không còn khả dụng.");
		}

		EAppointmentType type = req.getType() != null ? req.getType() : EAppointmentType.EXAMINATION;

		Appointment appt = new Appointment();

		appt.setTimeSlot(slot);
		appt.setDoctor(slot.getDoctor());
		appt.setPatient(patient);
		appt.setReason(req.getReason());
		appt.setType(type);
		appt.setCreatedAt(LocalDateTime.now());

		if (type == EAppointmentType.FOLLOW_UP || type == EAppointmentType.RESCHEDULE) {

			appt.setStatus(EAppointmentStatus.CONFIRMED);

		} else {

			appt.setStatus(EAppointmentStatus.PENDING);
		}

		appt = appointmentRepo.save(appt);

		slot.setStatus(ETimeSlotStatus.BOOKED);

		timeSlotRepo.save(slot);

		publisher.publishEvent(new AppointmentEvent(EAppointmentEventType.BOOKED, appt, "PATIENT", null));
		publisher.publishEvent(new DoctorDashboardChangedEvent(slot.getDoctor().getId()));
		return mapToDTO(appt);
	}
	
	@Override
	@Transactional
	public AppointmentDTO rebookSlot(AppointmentRequest req) {

		TimeSlot slot = timeSlotRepo.findById(req.getSlotId())
				.orElseThrow(() -> new RuntimeException("Slot not found"));

		if (slot.getStatus() != ETimeSlotStatus.AVAILABLE) {
			throw new RuntimeException("Slot not available");
		}

		slot.setStatus(ETimeSlotStatus.BOOKED);

		Patient patient = patientRepo.findById(req.getPatientId()).orElseThrow(
				() -> new AppException("Không tìm thấy bệnh nhân", "PATIENT_NOT_FOUND", HttpStatus.BAD_REQUEST));

		Appointment appt = new Appointment();
		appt.setTimeSlot(slot);
		appt.setDoctor(slot.getDoctor());
		appt.setPatient(patient);
		appt.setReason(req.getReason());
		appt.setType(req.getType());
		appt.setStatus(EAppointmentStatus.CONFIRMED);
		appt.setCreatedAt(LocalDateTime.now());

		appointmentRepo.save(appt);

		publisher.publishEvent(new AppointmentEvent(EAppointmentEventType.REBOOK, appt, "DOCTOR", null));
		publisher.publishEvent(
                new DoctorDashboardChangedEvent(
                        slot.getDoctor().getId()
                )
        );
		
		return mapToDTO(appt);
	}


	@Override
	@Transactional
	public void confirmAppointmentDoctor(Long doctor, Long appointmentId) {
		Appointment apt = appointmentRepo.findById(appointmentId).orElseThrow(
				() -> new AppException("Appointment không hợp lệ", "APPOINTMENT_NOT_FOUND", HttpStatus.BAD_REQUEST));
		if (doctor != apt.getDoctor().getId()) {
			throw new AppException("Appointment không hợp lệ", "APPOINTMENT_NOT_FOUND", HttpStatus.BAD_REQUEST);
		}

		LocalDateTime now = LocalDateTime.now();

		apt.setStatus(EAppointmentStatus.CONFIRMED);
		apt.setUpdatedAt(now);

		appointmentRepo.save(apt);
		
		publisher.publishEvent(new AppointmentEvent(EAppointmentEventType.CONFIRMED, apt, "DOCTOR", null));
		
	}

	@Override
	@Transactional
	public void checkIn(Long patientId, Long appointmentId) {
		Appointment apt = appointmentRepo.findById(appointmentId).orElseThrow(
				() -> new AppException("Appointment không hợp lệ", "APPOINTMENT_NOT_FOUND", HttpStatus.BAD_REQUEST));
		if (patientId != apt.getPatient().getId()) {
			throw new AppException("Appointment không hợp lệ", "APPOINTMENT_NOT_FOUND", HttpStatus.BAD_REQUEST);
		}

		LocalDateTime now = LocalDateTime.now();
		apt.setCheckedIn(true);
		apt.setUpdatedAt(now);

		appointmentRepo.save(apt);

		publisher.publishEvent(new AppointmentEvent(EAppointmentEventType.PATIENT_CHECK_IN, apt, "PATIENT", null));
		
	}

	@Override
	@Transactional
	public void completeAppointmentDoctor(Long doctorId, Long appointmentId) {
	    Appointment apt = appointmentRepo.findById(appointmentId)
	            .orElseThrow(() -> new AppException(
	                "Appointment không hợp lệ",
	                "APPOINTMENT_NOT_FOUND",
	                HttpStatus.BAD_REQUEST));

	    if (!doctorId.equals(apt.getDoctor().getId())) {
	        throw new AppException(
	            "Appointment không hợp lệ",
	            "APPOINTMENT_NOT_FOUND",
	            HttpStatus.BAD_REQUEST);
	    }

	    apt.setStatus(EAppointmentStatus.COMPLETED);
	    apt.setUpdatedAt(LocalDateTime.now());
	    appointmentRepo.save(apt);

	    publisher.publishEvent(new AppointmentEvent(EAppointmentEventType.COMPLETED, apt, "DOCTOR", null));
	    publisher.publishEvent(
                new DoctorDashboardChangedEvent(
                        apt.getDoctor().getId()
                )
        );
		
	}

	@Override
	public AppointmentDTO mapToDTO(Appointment appt) {

		Long recordId = null;
		Optional<MedicalRecord> existing = recordRepo.findByAppointmentId(appt.getId());

		if (existing.isPresent()) {
			recordId = existing.get().getId();
		}

		return AppointmentDTO.builder().id(appt.getId())

				.doctorId(appt.getDoctor() != null ? appt.getDoctor().getId() : null)
				.doctorName(appt.getDoctor() != null
						? appt.getDoctor().getFirstName() + " " + appt.getDoctor().getLastName()
						: null)
				.doctorAvatar(appt.getDoctor() != null ? appt.getDoctor().getAvatar() : null)
				.patientId(appt.getPatient() != null ? appt.getPatient().getId() : null)
				.patientAvatar(appt.getPatient() != null ? appt.getPatient().getAvatar() : null)
				.patientName(appt.getPatient() != null
						? appt.getPatient().getFirstName() + " " + appt.getPatient().getLastName()
						: null)
				.slotId(appt.getTimeSlot() != null ? appt.getTimeSlot().getId() : null)
				.slotTime(formatSlotTime(appt.getTimeSlot())).slotDate(formatSlotDate(appt.getTimeSlot()))
				.reason(appt.getReason()).type(appt.getType()).status(appt.getStatus()).createdAt(appt.getCreatedAt())

				.recordId(recordId).cancelBy(appt.getCancelBy()).cancelledAt(appt.getCancelledAt())
				.cancelReason(appt.getCancelReason()).checkedIn(appt.getCheckedIn()).build();
	}

	private String formatSlotTime(TimeSlot slot) {
		if (slot == null || slot.getStartTime() == null || slot.getEndTime() == null) {
			return null;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

		return slot.getStartTime().format(formatter) + " - " + slot.getEndTime().format(formatter);
	}

	private String formatSlotDate(TimeSlot slot) {
		if (slot == null || slot.getDate() == null) {
			return null;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return slot.getDate().format(formatter);
	}

	@Scheduled(cron = "0 0 * * * *")
	@Transactional
	public void expireAppointments() {
		appointmentRepo.expireAppointments();
	}
}
