package vn.wellcare4u.services;

import org.springframework.transaction.annotation.Transactional;

import vn.wellcare4u.entities.doctor.DoctorDashboardSnapshot;

public interface DoctorDashboardSnapshotService {

	void rebuildSnapshot(Long doctorId);

	DoctorDashboardSnapshot getSnapshot(Long doctorId);

}
