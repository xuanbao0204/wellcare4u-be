package vn.wellcare4u.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.enums.EAppointmentStatus;
import vn.wellcare4u.enums.EAppointmentType;
import vn.wellcare4u.models.dto.AppointmentDTO;
import vn.wellcare4u.models.request.AppointmentRequest;
import vn.wellcare4u.models.request.CancelAppointmentRequest;

public interface AppointmentService {

	AppointmentDTO bookSlot(AppointmentRequest req, Long patientId);

	List<AppointmentDTO> getAppointmentByPatient(Long patientId);

	Page<AppointmentDTO> getAppointmentByPatient(String patientEmail, EAppointmentStatus status, EAppointmentType type, Pageable pageable);

	Page<AppointmentDTO> getAppointmentByDoctor(String doctorEmail, EAppointmentStatus status, EAppointmentType type, Pageable pageable);

	void confirmAppointmentDoctor(Long doctor, Long appointmentId);

	void completeAppointmentDoctor(Long doctor, Long appointmentId);

	AppointmentDTO mapToDTO(Appointment appt);

	void cancel(Long userId, Long appointmentId, CancelAppointmentRequest req);

	void checkIn(Long patientId, Long appointmentId);

	AppointmentDTO rebookSlot(AppointmentRequest req);

}
