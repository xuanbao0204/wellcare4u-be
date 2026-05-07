package vn.wellcare4u.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>{

	Optional<Patient> findByAccount_Email(String email);

}
