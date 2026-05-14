package vn.wellcare4u.controllers.admin;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.wellcare4u.enums.EPostSortType;
import vn.wellcare4u.enums.ESpecialization;
import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.dto.admin.AdminPostDTO;
import vn.wellcare4u.models.dto.admin.DashboardStatsDTO;
import vn.wellcare4u.models.dto.forum.PostSummaryDTO;
import vn.wellcare4u.services.AdminService;
import vn.wellcare4u.services.ForumService;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminAPI {

	private final AdminService adminServ;
	private ForumService forumServ;
	
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
	
	@GetMapping("/posts")
	public ApiResponse<Page<PostSummaryDTO>> getAllPosts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, 
			@RequestParam(required = false) ESpecialization category,
			@RequestParam(required = false) String keyword, 
			@RequestParam(defaultValue = "NEWEST") EPostSortType sort) {
		return ApiResponse.<Page<PostSummaryDTO>>builder()
				.status(200)
				.message("Get posts successfully")
				.data(forumServ.getAllPosts(page, size, category, keyword, sort))
				.build();
	}
}
