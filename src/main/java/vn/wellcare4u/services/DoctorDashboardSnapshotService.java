package vn.wellcare4u.services;

import vn.wellcare4u.entities.doctor.DoctorDashboardSnapshot;

public interface DoctorDashboardSnapshotService {

	void rebuildSnapshot(Long doctorId);

	DoctorDashboardSnapshot getSnapshot(Long doctorId);

	String generateSummary(Long doctorId);

}
