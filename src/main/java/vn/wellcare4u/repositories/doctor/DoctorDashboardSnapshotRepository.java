package vn.wellcare4u.repositories.doctor;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.wellcare4u.entities.doctor.DoctorDashboardSnapshot;

public interface DoctorDashboardSnapshotRepository extends JpaRepository<DoctorDashboardSnapshot, Long> {
	
}