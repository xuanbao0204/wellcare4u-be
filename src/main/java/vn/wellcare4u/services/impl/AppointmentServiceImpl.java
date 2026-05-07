package vn.wellcare4u.services.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.entities.Patient;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.entities.doctor.TimeSlot;
import vn.wellcare4u.entities.medical.MedicalRecord;
import vn.wellcare4u.enums.EAppointmentStatus;
import vn.wellcare4u.enums.EAppointmentType;
import vn.wellcare4u.enums.ECancelBy;
import vn.wellcare4u.enums.ENotificationType;
import vn.wellcare4u.enums.ETimeSlotStatus;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.models.dto.AppointmentDTO;
import vn.wellcare4u.models.request.AppointmentRequest;
import vn.wellcare4u.models.request.CancelAppointmentRequest;
import vn.wellcare4u.models.request.NotificationRequest;
import vn.wellcare4u.repositories.AppointmentRepository;
import vn.wellcare4u.repositories.DoctorRepository;
import vn.wellcare4u.repositories.PatientRepository;
import vn.wellcare4u.repositories.doctor.TimeSlotRepository;
import vn.wellcare4u.repositories.medical.MedicalRecordRepository;
import vn.wellcare4u.services.AppointmentService;
import vn.wellcare4u.services.NotificationService;
import vn.wellcare4u.services.UserService;

@Service
public class AppointmentServiceImpl implements AppointmentService {

	@Autowired
	private TimeSlotRepository timeSlotRepo;
	@Autowired
	private AppointmentRepository appointmentRepo;

	@Autowired
	private PatientRepository patientRepo;
	
	@Autowired
	private DoctorRepository doctorRepo;

	@Autowired
	private UserService uServ;

	@Autowired
	private MedicalRecordRepository recordRepo;

	@Autowired
	private NotificationService notiServ;

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
		
		notiServ.send(
		        NotificationRequest.toUsers(
		        		List.of(
		            apt.getPatient().getId(), apt.getDoctor().getId()),
		            ENotificationType.WARNING,
		            "Cập nhật về lịch hẹn",
		            "Lịch hẹn với mã #" + apt.getId() + " đã bị hủy bởi " + (isDoctor ? "Bác sĩ" : "Bệnh nhân") + ". Xem chi tiết",
		            appointmentId
		        )
		    );
	}

//	@Override
//	public void cancelAppointment(Long patientId, Long appointmentId) {
//		Appointment apt = appointmentRepo.findById(appointmentId).orElseThrow(
//				() -> new AppException("Appointment không hợp lệ", "APPOINTMENT_NOT_FOUND", HttpStatus.BAD_REQUEST));
//		if (patientId != apt.getPatient().getId()) {
//			throw new AppException("Appointment không hợp lệ", "APPOINTMENT_NOT_FOUND", HttpStatus.BAD_REQUEST);
//		}
//
//		LocalDateTime appointmentDateTime = apt.getTimeSlot().getDate().atTime(apt.getTimeSlot().getStartTime());
//		LocalDateTime now = LocalDateTime.now();
//
//		LocalDateTime deadline = appointmentDateTime.minusHours(24);
//
//		if (now.isAfter(deadline)) {
//			throw new AppException("Đã quá hạn hủy đặt lịch", "CANCEL_TIME_EXPIRED", HttpStatus.BAD_REQUEST);
//		}
//
//		apt.setStatus(EAppointmentStatus.CANCELLED);
//		apt.setUpdatedAt(now);
//
//		appointmentRepo.save(apt);
//
//		TimeSlot slot = apt.getTimeSlot();
//		slot.setStatus(ETimeSlotStatus.AVAILABLE);
//		timeSlotRepo.save(slot);
//	}
//
//	@Override
//	public void cancelAppointmentDoctor(Long doctor, Long appointmentId) {
//		Appointment apt = appointmentRepo.findById(appointmentId).orElseThrow(
//				() -> new AppException("Appointment không hợp lệ", "APPOINTMENT_NOT_FOUND", HttpStatus.BAD_REQUEST));
//		if (doctor != apt.getDoctor().getId()) {
//			throw new AppException("Appointment không hợp lệ", "APPOINTMENT_NOT_FOUND", HttpStatus.BAD_REQUEST);
//		}
//
//		LocalDateTime appointmentDateTime = apt.getTimeSlot().getDate().atTime(apt.getTimeSlot().getStartTime());
//		LocalDateTime now = LocalDateTime.now();
//
//		LocalDateTime deadline = appointmentDateTime.minusHours(24);
//
//		if (apt.getStatus() == EAppointmentStatus.CONFIRMED)
//			throw new AppException("Không thể hủy vì đã confirm", "NO_CANCEL_BOOKED_SLOT", HttpStatus.BAD_REQUEST);
//
//		if (now.isAfter(deadline)) {
//			throw new AppException("Đã quá hạn hủy đặt lịch", "CANCEL_TIME_EXPIRED", HttpStatus.BAD_REQUEST);
//		}
//
//		apt.setStatus(EAppointmentStatus.CANCELLED);
//		apt.setUpdatedAt(now);
//
//		appointmentRepo.save(apt);
//
//		TimeSlot slot = apt.getTimeSlot();
//		slot.setStatus(ETimeSlotStatus.BLOCKED);
//		timeSlotRepo.save(slot);
//	}

	@Override
	@Transactional
	public AppointmentDTO bookSlot(AppointmentRequest req, Long patientId) {

		TimeSlot slot = timeSlotRepo.findById(req.getSlotId())
				.orElseThrow(() -> new RuntimeException("Slot not found"));

		if (slot.getStatus() != ETimeSlotStatus.AVAILABLE) {
			throw new RuntimeException("Slot not available");
		}

		slot.setStatus(ETimeSlotStatus.BOOKED);

		Patient patient = patientRepo.findById(patientId).orElseThrow(
				() -> new AppException("Không tìm thấy bệnh nhân", "PATIENT_NOT_FOUND", HttpStatus.BAD_REQUEST));

		Appointment appt = new Appointment();
		appt.setTimeSlot(slot);
		appt.setDoctor(slot.getDoctor());
		appt.setPatient(patient);
		appt.setReason(req.getReason());
		appt.setType(req.getType());
		if (req.getType() == EAppointmentType.FOLLOW_UP || req.getType() == EAppointmentType.RESCHEDULE)
			appt.setStatus(EAppointmentStatus.CONFIRMED);
		else
			appt.setStatus(EAppointmentStatus.PENDING);
		appt.setCreatedAt(LocalDateTime.now());

		appointmentRepo.save(appt);

		notiServ.send(
		        NotificationRequest.toUser(
		            appt.getPatient().getId(),
		            ENotificationType.INFO,
		            "Cập nhật về lịch hẹn",
		            "Lịch hẹn đã được tạo thành công",
		            null
		        )
		    );
		
		
		notiServ.send(
				NotificationRequest.toUser(
						slot.getDoctor().getId(),
						ENotificationType.INFO,
						"Thông báo lịch hẹn",
						"Có một lịch hẹn mới đang chờ bạn xử lý",
						null));

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

		notiServ.send(
		        NotificationRequest.toUser(
		            patient.getId(),
		            ENotificationType.INFO,
		            "Cập nhật về lịch hẹn",
		            "Lịch hẹn đã được bác sỹ tạo thành công do bị hủy",
		            null
		        )
		    );
		
		
		notiServ.send(
				NotificationRequest.toUser(
						slot.getDoctor().getId(),
						ENotificationType.INFO,
						"Thông báo lịch hẹn",
						"Bạn đã tạo thành công lịch hẹn lại cho lịch đã bị hủy",
						null));

		return mapToDTO(appt);
	}


	@Override
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
		
		notiServ.send(
		        NotificationRequest.toUser(
		            apt.getPatient().getId(),
		            ENotificationType.INFO,
		            "Cập nhật về lịch hẹn",
		            "Lịch hẹn với mã #" + apt.getId() + " đã được xác nhận",
		            appointmentId
		        )
		    );
	}

	@Override
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
		notiServ.send(
		        NotificationRequest.toUser(
		            apt.getDoctor().getId(),
		            ENotificationType.INFO,
		            "Cập nhật về lịch hẹn",
		            "Lịch hẹn với mã #" + apt.getId() + ":bệnh nhân đã đến",
		            appointmentId
		        )
		    );
	}

	@Override
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

	    notiServ.send(
	        NotificationRequest.toUser(
	            apt.getPatient().getId(),
	            ENotificationType.INFO,
	            "Cập nhật về lịch hẹn",
	            "Lịch hẹn với mã #" + apt.getId() + " đã hoàn thành",
	            appointmentId
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
