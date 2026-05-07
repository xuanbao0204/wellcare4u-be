package vn.wellcare4u.repositories.medical;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.medical.MedicalRecord;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long>{
	List<MedicalRecord> findByPatientId(Long patientId);
	Optional<MedicalRecord> findByAppointmentId(Long appointmentId);
	
	List<MedicalRecord> findByDoctorId(Long doctorId);
	
	@Query("""
		    SELECT r FROM MedicalRecord r
		    JOIN FETCH r.patient p
		    WHERE r.doctor.id = :doctorId
		    ORDER BY p.id ASC, r.createdAt DESC
		""")
		List<MedicalRecord> findAllByDoctorId(@Param("doctorId") Long doctorId);
	
	@Query("""
		    SELECT r FROM MedicalRecord r
		    JOIN FETCH r.patient p
		    WHERE r.doctor.id = :doctorId
		    AND r.patient.id = :patientId
		    ORDER BY r.createdAt DESC
		""")
	List<MedicalRecord> findAllByDoctorIdAndPatientId(
		    @Param("doctorId") Long doctorId,
		    @Param("patientId") Long patientId
		);
	
	@EntityGraph(attributePaths = {
	        "doctor",
	        "appointment",
	        "patient"
	})
	Optional<MedicalRecord> findDetailById(Long id);
}
