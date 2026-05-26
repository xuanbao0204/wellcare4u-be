package vn.wellcare4u.repositories.patient;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.wellcare4u.entities.patient.PatientDashboardSnapshot;

public interface PatientDashboardSnapshotRepository extends JpaRepository<PatientDashboardSnapshot, Long> {

}