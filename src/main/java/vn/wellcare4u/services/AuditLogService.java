package vn.wellcare4u.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.wellcare4u.entities.admin.AuditLog;
import vn.wellcare4u.models.dto.PageDTO;
import vn.wellcare4u.models.dto.admin.AuditLogDTO;

public interface AuditLogService {

	void createLog(AuditLog a);

	List<AuditLogDTO> getAllLog();

	PageDTO<AuditLogDTO> getAuditLogsPage(String keyword, Pageable pageable);

}
