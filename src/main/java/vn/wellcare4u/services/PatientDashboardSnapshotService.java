package vn.wellcare4u.services;

import vn.wellcare4u.entities.patient.PatientDashboardSnapshot;

public interface PatientDashboardSnapshotService {

	void rebuildSnapshot(Long patientId);

	PatientDashboardSnapshot getSnapshot(Long patientId);

}
