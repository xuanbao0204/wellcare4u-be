package vn.wellcare4u.controllers.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.wellcare4u.enums.EForumCategory;
import vn.wellcare4u.enums.EPostSortType;
import vn.wellcare4u.enums.ESpecialization;
import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.dto.PageDTO;
import vn.wellcare4u.models.dto.admin.AuditLogDTO;
import vn.wellcare4u.models.dto.admin.DashboardStatsDTO;
import vn.wellcare4u.models.dto.forum.PostSummaryDTO;
import vn.wellcare4u.models.response.TrendsResponseDTO;
import vn.wellcare4u.services.AdminReportService;
import vn.wellcare4u.services.AdminService;
import vn.wellcare4u.services.AuditLogService;
import vn.wellcare4u.services.ForumService;
import vn.wellcare4u.services.UserService;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminAPI {

	private final AdminService adminServ;
	private final ForumService forumServ;
	private final AdminReportService adminReportService;
	private final UserService userServ;
	private final AuditLogService logServ;
	
	@GetMapping("/dashboard/stats")
	public ApiResponse<DashboardStatsDTO> getAdminStats(Authentication auth) {
		if (!auth.isAuthenticated() || auth == null) {
			return ApiResponse.<DashboardStatsDTO>builder()
					.status(403)
					.errorCode("NOT_AUTHENTICATED")
					.message("not authenticated")
					.build();
		}
		return ApiResponse.<DashboardStatsDTO>builder()
				.status(200)
				.message("Get successfully")
				.data(adminServ.getDashboardStats())
				.build();
	}
	
	@GetMapping("/dashboard/trends")
	public ApiResponse<TrendsResponseDTO> getTrends(
	        @RequestParam(defaultValue = "WEEK") String period,
	        @RequestParam(defaultValue = "0")    int    offset) {
	    return ApiResponse.<TrendsResponseDTO>builder()
	    		.status(200)
	    		.data(adminServ.getTrends(period, offset))
	    		.build();
	}
	
	@GetMapping("/posts")
	public ApiResponse<Page<PostSummaryDTO>> getAllPosts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, 
			@RequestParam(required = false) EForumCategory category,
			@RequestParam(required = false) ESpecialization specialization,
			@RequestParam(required = false) String keyword, 
			@RequestParam(defaultValue = "NEWEST") EPostSortType sort) {
		return ApiResponse.<Page<PostSummaryDTO>>builder()
				.status(200)
				.message("Get posts successfully")
				.data(forumServ.getAllPosts(page, size, category, specialization, keyword, sort))
				.build();
	}
	
	@GetMapping("/analytics/xlsx")
	public ResponseEntity<byte[]> exportAnalyticsExcel(Authentication auth) {

		if (!auth.isAuthenticated() || auth == null) {
			return ResponseEntity.badRequest()
					.build();
		}
		
		long adminId = userServ.getIdFromEmail(auth.getName());

		byte[] file = adminReportService.exportAnalyticsExcel(adminId);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=analytics-report.xlsx")
				.contentType(
						MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(file);
	}
	
	@GetMapping("/audit-logs")
	public ApiResponse<PageDTO<AuditLogDTO>> getAuditLogs(Authentication auth, 
			@RequestParam(required = false) String keyword,
			@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
		if (!auth.isAuthenticated() || auth == null) {
			return ApiResponse.<PageDTO<AuditLogDTO>>builder()
					.status(403)
					.errorCode("NOT_AUTHENTICATED")
					.message("not authenticated")
					.build();
		}
		
		Pageable pageable = PageRequest.of(page, size);
		
		return ApiResponse.<PageDTO<AuditLogDTO>>builder()
				.status(200)
				.message("Get successfully")
				.data(logServ.getAuditLogsPage(keyword, pageable))
				.build();
	}
}
