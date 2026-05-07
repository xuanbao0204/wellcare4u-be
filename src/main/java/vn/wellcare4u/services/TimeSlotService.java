package vn.wellcare4u.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.transaction.Transactional;
import vn.wellcare4u.entities.doctor.DoctorSchedule;
import vn.wellcare4u.models.dto.doctor.TimeSlotDTO;

public interface TimeSlotService {

	void generateSlots(DoctorSchedule schedule, int daysAhead);

	void refreshSlots(DoctorSchedule schedule);

	void blockSlot(Long slotId);

	List<TimeSlotDTO> getSlotsByDoctor(Long doctorId, LocalDate from, LocalDate to);

	List<TimeSlotDTO> getAvailableSlots(Long doctorId, LocalDate date);

	void unblockSlot(Long id);

	void cancelOutdatedAvailableSlots(DoctorSchedule schedule, LocalTime newStart, LocalTime newEnd, int newDurationMinutes);

	void restoreBlockedSlots(Long doctorId, LocalDate date);

	void applyDayOff(Long doctorId, LocalDate date);

	void generateSlotsForDate(DoctorSchedule schedule, LocalDate date);

}
