package vn.wellcare4u.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.wellcare4u.entities.admin.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
	
	@Query("""
		    SELECT al FROM AuditLog al
		    WHERE (
		        :keyword IS NULL
		        OR LOWER(al.action) LIKE LOWER(CONCAT('%', :keyword, '%'))
		        OR LOWER(al.entityType) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    )
		""")
		Page<AuditLog> getAllAuditLogs(
		    @Param("keyword") String keyword,
		    Pageable pageable
		);
}