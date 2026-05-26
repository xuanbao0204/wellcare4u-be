package vn.wellcare4u.services;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import vn.wellcare4u.models.dto.admin.AdminAccountDTO;
import vn.wellcare4u.models.dto.admin.AdminPostDTO;
import vn.wellcare4u.models.dto.admin.DashboardStatsDTO;
import vn.wellcare4u.models.response.TrendsResponseDTO;

public interface AdminService {

	byte[] exportAppointmentsCsv();

	byte[] exportAccountsCsv();

	void deletePost(Long postId);

	Page<AdminPostDTO> getPosts(String keyword, String category, int page, int size);

	void unverifyDoctor(Long accountId);

	void verifyDoctor(Long accountId);

	Page<AdminAccountDTO> getAccounts(String role, String status, String keyword, int page, int size);

	DashboardStatsDTO getDashboardStats();

	TrendsResponseDTO getTrends(String period, int offset);

}
