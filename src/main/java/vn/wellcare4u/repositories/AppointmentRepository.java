package vn.wellcare4u.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.entities.doctor.DoctorDashboardSnapshot;
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

	@Query("""
			SELECT a FROM Appointment a
			WHERE a.patient.id = :patientId
			  AND a.timeSlot.date >= :today
			  AND a.status IN ('CONFIRMED', 'PENDING')
			ORDER BY a.timeSlot.date ASC, a.timeSlot.startTime ASC
			""")
	List<Appointment> findUpcomingByPatientId(@Param("patientId") Long patientId, @Param("today") LocalDate today,
			Pageable pageable);

	List<Appointment> findTop3ByPatientIdOrderByCreatedAtDesc(Long patientId);

	long countByPatientIdAndStatus(Long patientId, EAppointmentStatus status);

	long countByPatientId(Long patientId);
	
	long countByDoctorId(Long doctorId);

	@Query("""
			    SELECT a FROM Appointment a
			    JOIN a.timeSlot ts
			    WHERE a.patient.id = :patientId
			      AND a.status IN ('CONFIRMED', 'PENDING')
			      AND CAST(ts.date AS localdate) >= CURRENT_DATE
			    ORDER BY ts.date ASC, ts.startTime ASC
			    LIMIT 1
			""")
	Optional<Appointment> findUpcomingByPatientId(@Param("patientId") Long patientId);

	@Query("""
			    SELECT a FROM Appointment a
			    JOIN a.timeSlot ts
			    WHERE a.patient.id = :patientId
			    ORDER BY ts.date DESC, ts.startTime DESC
			    LIMIT 3
			""")
	List<Appointment> findTop3RecentByPatientId(@Param("patientId") Long patientId);
	
	@Query("""
		    SELECT COUNT(a)
		    FROM Appointment a
		    WHERE a.doctor.id = :doctorId
		    AND a.status = 'CANCELLED'
		""")
		Long countCancelledByDoctor(Long doctorId);
	
	@Query("""
		    SELECT COUNT(a)
		    FROM Appointment a
		    WHERE a.doctor.id = :doctorId
		    AND a.status = 'COMPLETED'
		""")
		Long countCompletedByDoctor(Long doctorId);
	
	@Query("""
		    SELECT COUNT(DISTINCT a.patient.id)
		    FROM Appointment a
		    WHERE a.doctor.id = :doctorId
		""")
		Long countPatientsByDoctor(Long doctorId);
	
	@Query("""
		    SELECT a
		    FROM Appointment a
		    WHERE a.doctor.id = :doctorId
		    ORDER BY a.updatedAt DESC
		""")
		List<Appointment> findRecentAppointments(Long doctorId, Pageable pageable);
	
	@Query("""
		    SELECT COUNT(a)
		    FROM Appointment a
		    WHERE a.doctor.id = :doctorId
		    AND a.timeSlot.date = CURRENT_DATE
		""")
		Long countTodayAppointments(Long doctorId);
	
	@Query("""
		    SELECT a
		    FROM Appointment a
		    WHERE a.doctor.id = :doctorId
		    AND a.status IN ('CONFIRMED', 'PENDING')
		    AND a.timeSlot.date >= CURRENT_DATE
		    ORDER BY a.timeSlot.date ASC,
		             a.timeSlot.startTime ASC
		""")
	List<Appointment> findUpcomingAppointments(Long doctorId, Pageable pageable);
	
	List<Appointment> findAllByDoctorId(Long doctorId);

	@Query("""
		    SELECT COUNT(DISTINCT a.patient.id)
		    FROM Appointment a
		    WHERE a.doctor.id = :doctorId
		""")
	long countDistinctPatientsByDoctorId(@Param("doctorId") Long doctorId);

	Optional<DoctorDashboardSnapshot> findTop5UpcomingByDoctorId(Long doctorId, LocalDate now);
	
	long countByDoctorIdAndStatus(
	        Long doctorId,
	        EAppointmentStatus status
	);
	
	long countByStatus(EAppointmentStatus status);
	
	@Query("""
		    SELECT DATE(a.createdAt), COUNT(a)
		    FROM Appointment a
		    WHERE a.createdAt >= :from
		    GROUP BY DATE(a.createdAt)
		    ORDER BY DATE(a.createdAt)
		""")
		List<Object[]> countAppointmentsGroupedByDate(@Param("from") LocalDateTime from);
		
		// AppointmentRepository.java

		@Query("""
		    SELECT DATE(a.createdAt), COUNT(a)
		    FROM Appointment a
		    WHERE a.createdAt >= :from AND a.createdAt < :to
		    GROUP BY DATE(a.createdAt)
		""")
		List<Object[]> countAppointmentsGroupedByDate(
		    @Param("from") LocalDateTime from,
		    @Param("to")   LocalDateTime to
		);

		@Query("""
		    SELECT DATE_FORMAT(a.createdAt, '%Y-%m'), COUNT(a)
		    FROM Appointment a
		    WHERE a.createdAt >= :from AND a.createdAt < :to
		    GROUP BY DATE_FORMAT(a.createdAt, '%Y-%m')
		""")
		List<Object[]> countAppointmentsGroupedByMonth(
		    @Param("from") LocalDateTime from,
		    @Param("to")   LocalDateTime to
		);
}
