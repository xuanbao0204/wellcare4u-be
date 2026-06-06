package vn.wellcare4u.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.wellcare4u.entities.Account;
import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.entities.ForumPost;
import vn.wellcare4u.entities.Notification;
import vn.wellcare4u.entities.User;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.enums.EAccountStatus;
import vn.wellcare4u.enums.EAppointmentStatus;
import vn.wellcare4u.enums.EForumCategory;
import vn.wellcare4u.enums.EPostSortType;
import vn.wellcare4u.enums.ERole;
import vn.wellcare4u.enums.ESpecialization;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.models.dto.DashboardTrendPointDTO;
import vn.wellcare4u.models.dto.admin.AdminAccountDTO;
import vn.wellcare4u.models.dto.admin.AdminPostDTO;
import vn.wellcare4u.models.dto.admin.DashboardStatsDTO;
import vn.wellcare4u.models.response.TrendsResponseDTO;
import vn.wellcare4u.repositories.AccountRepository;
import vn.wellcare4u.repositories.AppointmentRepository;
import vn.wellcare4u.repositories.DoctorRepository;
import vn.wellcare4u.repositories.NotificationRepository;
import vn.wellcare4u.repositories.forum.ForumCommentRepository;
import vn.wellcare4u.repositories.forum.ForumPostRepository;
import vn.wellcare4u.services.AdminService;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AccountRepository accountRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final ForumPostRepository forumPostRepository;
    private final ForumCommentRepository forumCommentRepository;
    private final NotificationRepository notificationRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public DashboardStatsDTO getDashboardStats() {

        long totalAccounts = accountRepository.count();

        long totalPatients = accountRepository.countByRole(ERole.PATIENT);
        long totalDoctors = accountRepository.countByRole(ERole.DOCTOR);
        long totalAdmins = accountRepository.countByRole(ERole.ADMIN);

        long activeAccounts = accountRepository.countByStatus(EAccountStatus.ACTIVE);
        long inactiveAccounts = accountRepository.countByStatus(EAccountStatus.INACTIVE);
        long lockedAccounts = accountRepository.countByStatus(EAccountStatus.LOCKED);

        long verifiedDoctors = doctorRepository.countByVerifiedTrue();
        long pendingVerificationDoctors = doctorRepository.countByVerifiedFalse();

        long totalAppointments = appointmentRepository.count();

        long pendingAppointments =
                appointmentRepository.countByStatus(EAppointmentStatus.PENDING);

        long completedAppointments =
                appointmentRepository.countByStatus(EAppointmentStatus.COMPLETED);

        long cancelledAppointments =
                appointmentRepository.countByStatus(EAppointmentStatus.CANCELLED);

        long totalPosts = forumPostRepository.count();
        long totalComments = forumCommentRepository.count();

        List<Notification> notifications =
                notificationRepository.findAll(
                        PageRequest.of(
                                0,
                                5,
                                Sort.by(Sort.Direction.DESC, "createdAt")
                        )
                ).getContent();

        List<DashboardStatsDTO.RecentNotificationDTO> recentNotifications =
                notifications.stream()
                        .map(n -> DashboardStatsDTO.RecentNotificationDTO.builder()
                                .id(n.getId())
                                .title(n.getTitle())
                                .content(n.getContent())
                                .type(n.getType().name())
                                .createdAt(
                                        n.getCreatedAt() != null
                                                ? n.getCreatedAt().format(FMT)
                                                : ""
                                )
                                .build())
                        .toList();
        
        LocalDateTime from = LocalDate.now()
                .minusDays(6)
                .atStartOfDay();

        List<Object[]> accountStats =
                accountRepository.countAccountsGroupedByDate(from);

        List<Object[]> appointmentStats =
                appointmentRepository.countAppointmentsGroupedByDate(from);

        Map<String, Long> accountMap = new HashMap<>();
        Map<String, Long> appointmentMap = new HashMap<>();

        for (Object[] row : accountStats) {
            accountMap.put(
                    row[0].toString(),
                    ((Number) row[1]).longValue()
            );
        }

        for (Object[] row : appointmentStats) {
            appointmentMap.put(
                    row[0].toString(),
                    ((Number) row[1]).longValue()
            );
        }

//        List<DashboardTrendPointDTO> trends = new ArrayList<>();
//
//        for (int i = 6; i >= 0; i--) {
//
//            LocalDate date = LocalDate.now().minusDays(i);
//
//            String key = date.toString();
//
//            trends.add(
//                    DashboardTrendPointDTO.builder()
//                            .label(date.format(DateTimeFormatter.ofPattern("dd/MM")))
//                            .users(accountMap.getOrDefault(key, 0L))
//                            .appointments(
//                                    appointmentMap.getOrDefault(key, 0L)
//                            )
//                            .build()
//            );
//        }

        return DashboardStatsDTO.builder()
                .totalAccounts(totalAccounts)
                .totalPatients(totalPatients)
                .totalDoctors(totalDoctors)
                .totalAdmins(totalAdmins)

                .activeAccounts(activeAccounts)
                .inactiveAccounts(inactiveAccounts)
                .lockedAccounts(lockedAccounts)

                .verifiedDoctors(verifiedDoctors)
                .pendingVerificationDoctors(pendingVerificationDoctors)

                .totalAppointments(totalAppointments)
                .pendingAppointments(pendingAppointments)
                .completedAppointments(completedAppointments)
                .cancelledAppointments(cancelledAppointments)

                .totalPosts(totalPosts)
                .totalComments(totalComments)

                .recentNotifications(recentNotifications)
//                .trends(trends)
                .build();
    }
    
    private static final DateTimeFormatter DAY_FMT   = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MM/yyyy");

    @Override
	public TrendsResponseDTO getTrends(String period, int offset) {
        return switch (period.toUpperCase()) {
            case "WEEK"  -> buildWeekTrends(offset);
            case "MONTH" -> buildMonthTrends(offset);
            case "YEAR"  -> buildYearTrends(offset);
            default      -> buildWeekTrends(offset);
        };
    }

    private TrendsResponseDTO buildWeekTrends(int offset) {
        LocalDate weekStart = LocalDate.now()
                .with(DayOfWeek.MONDAY)
                .minusWeeks(offset);
        LocalDate weekEnd   = weekStart.plusDays(7);

        LocalDateTime from = weekStart.atStartOfDay();
        LocalDateTime to   = weekEnd.atStartOfDay();

        Map<String, Long> accMap  = toMap(accountRepository.countAccountsGroupedByDate(from, to));
        Map<String, Long> apptMap = toMap(appointmentRepository.countAppointmentsGroupedByDate(from, to));

        List<DashboardTrendPointDTO> trends = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = weekStart.plusDays(i);
            trends.add(DashboardTrendPointDTO.builder()
                    .label(d.format(DAY_FMT))
                    .users(accMap.getOrDefault(d.toString(), 0L))
                    .appointments(apptMap.getOrDefault(d.toString(), 0L))
                    .build());
        }

        int weekOfYear = weekStart.get(WeekFields.ISO.weekOfWeekBasedYear());
        return TrendsResponseDTO.builder()
                .periodLabel("Tuần " + weekOfYear + "/" + weekStart.getYear())
                .hasPrev(true)
                .hasNext(offset > 0)
                .trends(trends)
                .build();
    }

    private TrendsResponseDTO buildMonthTrends(int offset) {
        YearMonth ym    = YearMonth.now().minusMonths(offset);
        LocalDate first = ym.atDay(1);
        LocalDate last  = ym.atEndOfMonth();

        LocalDateTime from = first.atStartOfDay();
        LocalDateTime to   = last.plusDays(1).atStartOfDay();

        Map<String, Long> accMap  = toMap(accountRepository.countAccountsGroupedByDate(from, to));
        Map<String, Long> apptMap = toMap(appointmentRepository.countAppointmentsGroupedByDate(from, to));

        List<DashboardTrendPointDTO> trends = new ArrayList<>();
        for (LocalDate d = first; !d.isAfter(last); d = d.plusDays(1)) {
            trends.add(DashboardTrendPointDTO.builder()
                    .label(d.format(DAY_FMT))
                    .users(accMap.getOrDefault(d.toString(), 0L))
                    .appointments(apptMap.getOrDefault(d.toString(), 0L))
                    .build());
        }

        return TrendsResponseDTO.builder()
                .periodLabel("Tháng " + ym.format(MONTH_FMT))
                .hasPrev(true)
                .hasNext(offset > 0)
                .trends(trends)
                .build();
    }

    private TrendsResponseDTO buildYearTrends(int offset) {
        int year = LocalDate.now().getYear() - offset;

        LocalDateTime from = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime to   = LocalDate.of(year + 1, 1, 1).atStartOfDay();

        Map<String, Long> accMap  = toMap(accountRepository.countAccountsGroupedByMonth(from, to));
        Map<String, Long> apptMap = toMap(appointmentRepository.countAppointmentsGroupedByMonth(from, to));

        List<DashboardTrendPointDTO> trends = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            String key   = String.format("%d-%02d", year, m);
            String label = String.format("%02d/%d", m, year);
            trends.add(DashboardTrendPointDTO.builder()
                    .label(label)
                    .users(accMap.getOrDefault(key, 0L))
                    .appointments(apptMap.getOrDefault(key, 0L))
                    .build());
        }

        return TrendsResponseDTO.builder()
                .periodLabel("Năm " + year)
                .hasPrev(true)
                .hasNext(offset > 0)
                .trends(trends)
                .build();
    }

    private Map<String, Long> toMap(List<Object[]> rows) {
        Map<String, Long> map = new HashMap<>();
        for (Object[] row : rows) {
            map.put(row[0].toString(), ((Number) row[1]).longValue());
        }
        return map;
    }

    @Override
    public Page<AdminAccountDTO> getAccounts(String role, String status, String keyword, int page, int size) {
        List<Account> all = accountRepository.findAll();

        List<Account> filtered = all.stream()
                .filter(a -> {
                    if (role != null && !role.isBlank()) {
                        try {
                            return a.getRole() == ERole.valueOf(role.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            return false;
                        }
                    }
                    return true;
                })
                .filter(a -> {
                    if (status != null && !status.isBlank()) {
                        try {
                            return a.getStatus() == EAccountStatus.valueOf(status.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            return false;
                        }
                    }
                    return true;
                })
                .filter(a -> {
                    if (keyword != null && !keyword.isBlank()) {
                        String kw = keyword.toLowerCase();
                        String email = a.getEmail() != null ? a.getEmail().toLowerCase() : "";
                        User user = a.getUser();
                        String name = "";
                        if (user != null) {
                            name = ((user.getFirstName() != null ? user.getFirstName() : "") + " "
                                    + (user.getLastName() != null ? user.getLastName() : "")).toLowerCase();
                        }
                        return email.contains(kw) || name.contains(kw);
                    }
                    return true;
                })
                .collect(Collectors.toList());

        int start = page * size;
        int end = Math.min(start + size, filtered.size());
        List<Account> pageContent = (start >= filtered.size()) ? new ArrayList<>() : filtered.subList(start, end);

        List<AdminAccountDTO> dtos = pageContent.stream()
                .map(this::toAdminAccountDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, PageRequest.of(page, size), filtered.size());
    }

    @Override
    @Transactional
    public void verifyDoctor(Long accountId) {
        Doctor doctor = doctorRepository.findAll().stream()
                .filter(d -> d.getAccount() != null && d.getAccount().getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new AppException("Không tìm thấy bác sĩ", "DOCTOR_NOT_FOUND", HttpStatus.NOT_FOUND));
        doctor.setVerified(true);
        doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public void unverifyDoctor(Long accountId) {
        Doctor doctor = doctorRepository.findAll().stream()
                .filter(d -> d.getAccount() != null && d.getAccount().getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new AppException("Không tìm thấy bác sĩ", "DOCTOR_NOT_FOUND", HttpStatus.NOT_FOUND));
        doctor.setVerified(false);
        doctorRepository.save(doctor);
    }

    @Override
	public Page<AdminPostDTO> getAllPosts(int page, int size, EForumCategory category, ESpecialization specialization,
			String keyword, EPostSortType sort) {

		Sort sorting = switch (sort) {
		case MOST_LIKED -> Sort.by("likes").descending();
		case MOST_VIEWED -> Sort.by("viewCount").descending();
		case MOST_COMMENTED -> Sort.by("comments.size").descending();
		default -> Sort.by("createdAt").descending(); // NEWEST
		};

		Pageable pageable = PageRequest.of(page, size, sorting);

		Page<ForumPost> posts;
		if (keyword != null && !keyword.isBlank()) {

			posts = forumPostRepository.searchWithFilter(keyword, category, specialization, pageable);

		} else {

			posts = forumPostRepository.filterPosts(category, specialization, pageable);
		}

		return posts.map(this::toAdminPostDTO);
	}

    @Override
    @Transactional
    public void deletePost(Long postId) {
        if (!forumPostRepository.existsById(postId)) {
            throw new AppException("Bài viết không tồn tại", "POST_NOT_FOUND", HttpStatus.NOT_FOUND);
        }
        forumPostRepository.deleteById(postId);
    }

    @Override
    public byte[] exportAccountsCsv() {
        List<Account> accounts = accountRepository.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(baos)) {
            pw.println("ID,Email,Vai trò,Trạng thái,Họ,Tên,Giới tính,Số điện thoại,Ngày tạo,Đăng nhập lần cuối");
            for (Account a : accounts) {
                User u = a.getUser();
                pw.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        a.getId(),
                        esc(a.getEmail()),
                        a.getRole() != null ? a.getRole().name() : "",
                        a.getStatus() != null ? a.getStatus().name() : "",
                        u != null && u.getFirstName() != null ? esc(u.getFirstName()) : "",
                        u != null && u.getLastName() != null ? esc(u.getLastName()) : "",
                        u != null && u.getGender() != null ? u.getGender() : "",
                        u != null && u.getPhone() != null ? u.getPhone() : "",
                        a.getCreatedAt() != null ? a.getCreatedAt().format(FMT) : "",
                        a.getLastLoginAt() != null ? a.getLastLoginAt().format(FMT) : "");
            }
        }
        return baos.toByteArray();
    }

    @Override
    public byte[] exportAppointmentsCsv() {
        List<Appointment> appointments = appointmentRepository.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(baos)) {
            pw.println("ID,Bệnh nhân,Bác sĩ,Loại,Trạng thái,Ngày tạo");
            for (Appointment a : appointments) {
                String patientName = a.getPatient() != null
                        ? esc(a.getPatient().getFirstName() + " " + a.getPatient().getLastName()) : "";
                String doctorName = a.getDoctor() != null
                        ? esc(a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName()) : "";
                pw.printf("%d,%s,%s,%s,%s,%s%n",
                        a.getId(),
                        patientName,
                        doctorName,
                        a.getType() != null ? a.getType().name() : "",
                        a.getStatus() != null ? a.getStatus().name() : "",
                        a.getCreatedAt() != null ? a.getCreatedAt().format(FMT) : "");
            }
        }
        return baos.toByteArray();
    }

    private AdminAccountDTO toAdminAccountDTO(Account a) {
        User user = a.getUser();
        AdminAccountDTO.AdminAccountDTOBuilder builder = AdminAccountDTO.builder()
                .id(a.getId())
                .email(a.getEmail())
                .role(a.getRole() != null ? a.getRole().name() : null)
                .status(a.getStatus() != null ? a.getStatus().name() : null)
                .createdAt(a.getCreatedAt() != null ? a.getCreatedAt().format(FMT) : null)
                .lastLoginAt(a.getLastLoginAt() != null ? a.getLastLoginAt().format(FMT) : null);

        if (user != null) {
            builder.userId(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .gender(user.getGender())
                    .phone(user.getPhone())
                    .avatar(user.getAvatar())
                    .dob(user.getDob() != null ? user.getDob().format(DATE_FMT) : null);

            if (a.getRole() == ERole.DOCTOR && user instanceof Doctor doc) {
                builder.verified(doc.isVerified())
                        .specialization(doc.getSpecialization() != null ? doc.getSpecialization().name() : null)
                        .experienceYears(doc.getExperienceYears())
                        .clinicAddress(doc.getClinicAddress())
                        .consultationFee(doc.getConsultationFee())
                        .certification(doc.getCertification());
            }
        }

        return builder.build();
    }

    private AdminPostDTO toAdminPostDTO(ForumPost p) {
        String authorName = p.isAnonymous() ? "Ẩn danh" :
                (p.getAuthor() != null ? p.getAuthor().getFirstName() + " " + p.getAuthor().getLastName() : "N/A");
        String authorEmail = (p.isAnonymous() || p.getAuthor() == null || p.getAuthor().getAccount() == null)
                ? "" : p.getAuthor().getAccount().getEmail();
        String content = p.getContent();
        String preview = content != null && content.length() > 200 ? content.substring(0, 200) + "..." : content;

        return AdminPostDTO.builder()
                .id(p.getId())
                .title(p.getTitle())
                .contentPreview(preview)
                .category(p.getCategory() != null ? p.getCategory().name() : null)
                .authorName(authorName)
                .authorEmail(authorEmail)
                .isAnonymous(p.isAnonymous())
                .viewCount(p.getViewCount())
                .likes(p.getLikes())
                .commentCount(p.getCommentCount())
                .tags(p.getTags())
                .createdAt(p.getCreatedAt() != null ? p.getCreatedAt().format(FMT) : null)
                .build();
    }

    private String esc(String s) {
        if (s == null) return "";
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
}