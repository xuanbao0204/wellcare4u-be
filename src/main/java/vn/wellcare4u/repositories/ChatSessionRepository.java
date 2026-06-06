package vn.wellcare4u.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.ChatSession;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

	Optional<ChatSession> findByPatientId(Long patientId);

	@Modifying
	@Query("""
			    delete from ChatSession c
			    where c.updatedAt < :expired
			""")
	void deleteExpired(LocalDateTime expired);
	
    void deleteByPatientId(Long patientId);
}