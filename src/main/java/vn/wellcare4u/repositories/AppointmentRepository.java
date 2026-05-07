package vn.wellcare4u.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.enums.EAppointmentStatus;
import vn.wellcare4u.enums.EAppointmentType;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

	List<Appointment> findByPatientId(Long patientId);

	@Query("""
			    SELECT a FROM Appointment a
			    WHERE a.patient.id = :patientId
			    AND (:status IS NULL OR a.status = :status)
			    AND (:type IS NULL OR a.type = :type)
			""")
	Page<Appointment> findByPatientIdAndFilters(Long patientId, EAppointmentStatus status, EAppointmentType type,
			Pageable pageable);

	@Query("""
			    SELECT a FROM Appointment a
			    WHERE a.doctor.id = :doctorId
			    AND (:status IS NULL OR a.status = :status)
			    AND (:type IS NULL OR a.type = :type)
			""")
	Page<Appointment> findByDoctorIdAndFilters(Long doctorId, EAppointmentStatus status, EAppointmentType type,
			Pageable pageable);

	@Modifying
	@Query(value = """
			    UPDATE appointment a
			    JOIN time_slot t ON a.time_slot_id = t.id
			    SET a.status = 'EXPIRED',
			   a.updated_at = NOW()
			    WHERE a.status IN ('PENDING', 'CONFIRMED')
			    AND TIMESTAMP(t.date, t.end_time) < NOW()
			""", nativeQuery = true)
	int expireAppointments();
}
