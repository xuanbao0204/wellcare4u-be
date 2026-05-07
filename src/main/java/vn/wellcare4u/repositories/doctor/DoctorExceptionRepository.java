package vn.wellcare4u.repositories.doctor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.doctor.DoctorException;

@Repository
public interface DoctorExceptionRepository extends JpaRepository<DoctorException, Long>{

	 boolean existsByDoctorIdAndDate(Long doctorId, LocalDate date);

	 List<DoctorException> findByDoctorId(Long doctorId);
	 
	 Optional<DoctorException> findByDoctorIdAndDate(Long doctorId, LocalDate date);
}
