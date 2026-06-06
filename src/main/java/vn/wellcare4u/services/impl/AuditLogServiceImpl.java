package vn.wellcare4u.services.impl;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.wellcare4u.entities.admin.AuditLog;
import vn.wellcare4u.models.dto.PageDTO;
import vn.wellcare4u.models.dto.admin.AuditLogDTO;
import vn.wellcare4u.repositories.AuditLogRepository;
import vn.wellcare4u.services.AuditLogService;
import vn.wellcare4u.services.UserService;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService{

	private final AuditLogRepository logRepo;
	private final UserService uServ;
	
	@Override
	public void createLog(AuditLog a) {
		
		logRepo.save(a);
	}
	
	@Override
	public List<AuditLogDTO> getAllLog(){
		List<AuditLog> logs = logRepo.findAll();
		return logs.stream().map(this::mapToDTO).toList();
	}
	
	@Override
	public PageDTO<AuditLogDTO> getAuditLogsPage(String keyword, Pageable pageable){
		return PageDTO.from(logRepo.getAllAuditLogs(keyword, pageable).map(this::mapToDTO));
	}
	
	private AuditLogDTO mapToDTO(AuditLog a) {
		return AuditLogDTO.builder()
				.id(a.getId())
				.action(a.getAction())
				.actor(uServ.convertToDTO(a.getActor()))
				.entityId(a.getEntityId())
				.entityType(a.getEntityType())
				.timestamp(a.getTimestamp())
				.build();
	}
}
