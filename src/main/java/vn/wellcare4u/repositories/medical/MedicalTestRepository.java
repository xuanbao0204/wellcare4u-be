package vn.wellcare4u.repositories.medical;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.medical.MedicalTest;

@Repository
public interface MedicalTestRepository extends JpaRepository<MedicalTest, Long>{

	List<MedicalTest> findByMedicalRecordId(Long recordId);
}
