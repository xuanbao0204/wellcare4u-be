package vn.wellcare4u.repositories.medical;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.medical.PrescriptionItem;

@Repository
public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, Long>{

	@Query("""
		    SELECT pi FROM PrescriptionItem pi
		    JOIN pi.prescription p
		    WHERE p.medicalRecord.id = :recordId
		""")
		List<PrescriptionItem> findByRecordId(Long recordId);
}
