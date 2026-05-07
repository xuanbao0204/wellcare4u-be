package vn.wellcare4u.repositories.doctor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.doctor.DoctorSchedule;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long>{
	List<DoctorSchedule> findByDoctorId(Long doctorId);
	List<DoctorSchedule> findByDoctorIdAndIsActiveTrue(Long doctorId);
	
	List<DoctorSchedule> findByIsActiveTrue();
	
	List<DoctorSchedule> findByDoctorIdAndDayOfWeekAndIsActiveTrue(Long doctorId, Integer dayOfWeek);
}
