package vn.wellcare4u.repositories.medical;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.medical.VitalSign;

@Repository
public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {

	Optional<VitalSign> findTopByMedicalRecordIdOrderByTimestampDesc(Long recordId);

	@Query("""
			    SELECT v FROM VitalSign v
			    WHERE v.patient.id = :patientId
			    ORDER BY v.timestamp DESC
			""")
	List<VitalSign> findByPatientIdOrderByTimestampDesc(Long patientId);

	@Query("""
			    SELECT v FROM VitalSign v
			    WHERE v.patient.id IN :patientIds
			    ORDER BY v.patient.id ASC, v.timestamp DESC
			""")
	List<VitalSign> findByPatientIdInOrderByTimestampDesc(@Param("patientIds") List<Long> patientIds);

	@Query("""
			    SELECT v FROM VitalSign v
			    WHERE v.patient.id = :patientId
			    ORDER BY v.patient.id ASC, v.timestamp DESC
			""")
	List<VitalSign> findByPatientIdInOrderByTimestampDesc(@Param("patientId")Long patientId);
}
