package vn.wellcare4u.models.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.wellcare4u.models.dto.DashboardTrendPointDTO;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    // Tổng quan tài khoản
    private long totalAccounts;
    private long totalPatients;
    private long totalDoctors;
    private long totalAdmins;

    // Trạng thái tài khoản
    private long activeAccounts;
    private long inactiveAccounts;
    private long lockedAccounts;

    // Bác sĩ
    private long verifiedDoctors;
    private long pendingVerificationDoctors;

    // Lịch hẹn
    private long totalAppointments;
    private long pendingAppointments;
    private long completedAppointments;
    private long cancelledAppointments;

    // Diễn đàn
    private long totalPosts;
    private long totalComments;

    // Thông báo mới nhất
    private List<RecentNotificationDTO> recentNotifications;
    
//    private List<DashboardTrendPointDTO> trends;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentNotificationDTO {
        private Long id;
        private String title;
        private String content;
        private String type;
        private String createdAt;
    }
}