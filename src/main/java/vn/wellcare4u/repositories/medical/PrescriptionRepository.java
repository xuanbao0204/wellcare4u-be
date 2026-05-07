package vn.wellcare4u.repositories.medical;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.medical.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long>{

}
