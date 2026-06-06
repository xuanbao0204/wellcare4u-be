package vn.wellcare4u.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.enums.ESpecialization;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>{

	Optional<Doctor> findByAccount_Email(String email);
	
	@Query("""
		    SELECT d FROM Doctor d
		    WHERE (:keyword IS NULL OR 
		           LOWER(d.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) 
		        OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')))
		      AND (:specialization IS NULL OR d.specialization = :specialization)
		      AND (:location IS NULL OR LOWER(d.clinicAddress) LIKE LOWER(CONCAT('%', :location, '%')))
		      AND (:onlyVerified IS NULL OR d.verified = :onlyVerified)
		""")
		Page<Doctor> searchDoctors(
		        @Param("keyword") String keyword,
		        @Param("specialization") String specialization,
		        @Param("location") String location,
		        @Param("onlyVerified") Boolean onlyVerified,
		        Pageable pageable
		);
	
	long countByVerifiedTrue();

    long countByVerifiedFalse();
    
    List<Doctor> findBySpecializationAndVerifiedTrue(ESpecialization specialization);
}
