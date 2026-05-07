package vn.wellcare4u.repositories.doctor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.doctor.TimeSlot;
import vn.wellcare4u.enums.ETimeSlotStatus;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

	List<TimeSlot> findByDoctorIdAndDate(Long doctorId, LocalDate date);

	boolean existsByDoctorIdAndDateAndStartTime(Long doctorId, LocalDate date, LocalTime startTime);

	@Modifying
	@Query("""
	    DELETE FROM TimeSlot t
	    WHERE t.status = 'AVAILABLE'
	    AND (
	        t.date < CURRENT_DATE OR
	        (t.date = CURRENT_DATE AND t.endTime < CURRENT_TIME)
	        )
	    AND NOT EXISTS(
			    SELECT 1 FROM Appointment a
				WHERE a.timeSlot.id = t.id
			)
	""")
	void deleteAvailableSlots();

	@Query("""
			    SELECT t FROM TimeSlot t
			    WHERE t.doctor.id = :doctorId
			    AND t.date = :date
			    AND t.status = 'AVAILABLE'
			    AND (
			        :date > CURRENT_DATE OR
			        ( :date = CURRENT_DATE AND t.startTime > CURRENT_TIME )
			    )
			""")
	List<TimeSlot> findAvailableSlotsValid(Long doctorId, LocalDate date);

	List<TimeSlot> findByDoctorIdAndDateBetween(Long doctorId, LocalDate from, LocalDate to);

	@Query("""
			    SELECT MAX(t.date)
			    FROM TimeSlot t
			    WHERE t.doctor.id = :doctorId
			""")
	LocalDate findMaxDateByDoctor(Long doctorId);

	List<TimeSlot> findByScheduleIdAndDateGreaterThanEqualAndStatus(Long id, LocalDate today,
			ETimeSlotStatus available);

}
