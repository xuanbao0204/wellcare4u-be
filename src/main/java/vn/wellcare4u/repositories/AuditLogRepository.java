package vn.wellcare4u.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.wellcare4u.entities.admin.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}