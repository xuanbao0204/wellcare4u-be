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
import vn.wellcare4u.enums.ERole;
import vn.wellcare4u.enums.ESpecialization;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.models.dto.admin.AdminAccountDTO;
import vn.wellcare4u.models.dto.admin.AdminPostDTO;
import vn.wellcare4u.models.dto.admin.DashboardStatsDTO;
import vn.wellcare4u.repositories.AccountRepository;
import vn.wellcare4u.repositories.AppointmentRepository;
import vn.wellcare4u.repositories.DoctorRepository;
import vn.wellcare4u.repositories.NotificationRepository;
import vn.wellcare4u.repositories.forum.ForumCommentRepository;
import vn.wellcare4u.repositories.forum.ForumPostRepository;
import vn.wellcare4u.services.AdminService;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
        List<Account> allAccounts = accountRepository.findAll();

        long totalAccounts = allAccounts.size();
        long totalPatients = allAccounts.stream().filter(a -> a.getRole() == ERole.PATIENT).count();
        long totalDoctors = allAccounts.stream().filter(a -> a.getRole() == ERole.DOCTOR).count();
        long totalAdmins = allAccounts.stream().filter(a -> a.getRole() == ERole.ADMIN).count();

        long activeAccounts = allAccounts.stream().filter(a -> a.getStatus() == EAccountStatus.ACTIVE).count();
        long inactiveAccounts = allAccounts.stream().filter(a -> a.getStatus() == EAccountStatus.INACTIVE).count();
        long lockedAccounts = allAccounts.stream().filter(a -> a.getStatus() == EAccountStatus.LOCKED).count();

        List<Doctor> doctors = doctorRepository.findAll();
        long verifiedDoctors = doctors.stream().filter(Doctor::isVerified).count();
        long pendingVerificationDoctors = doctors.stream().filter(d -> !d.isVerified()).count();

        List<Appointment> appointments = appointmentRepository.findAll();
        long totalAppointments = appointments.size();
        long pendingAppointments = appointments.stream()
                .filter(a -> a.getStatus().name().equals("PENDING")).count();
        long completedAppointments = appointments.stream()
                .filter(a -> a.getStatus().name().equals("COMPLETED")).count();
        long cancelledAppointments = appointments.stream()
                .filter(a -> a.getStatus().name().equals("CANCELLED")).count();

        long totalPosts = forumPostRepository.count();
        long totalComments = forumCommentRepository.count();

        // Recent notifications
        List<Notification> notifications = notificationRepository.findAll(
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();

        List<DashboardStatsDTO.RecentNotificationDTO> recentNotifications = notifications.stream()
                .map(n -> DashboardStatsDTO.RecentNotificationDTO.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .content(n.getContent())
                        .type(n.getType().name())
                        .createdAt(n.getCreatedAt() != null ? n.getCreatedAt().format(FMT) : "")
                        .build())
                .collect(Collectors.toList());

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
                .build();
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

    // ─── Posts ────────────────────────────────────────────────────────────────

    @Override
    public Page<AdminPostDTO> getPosts(String keyword, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ForumPost> posts;

        if (keyword != null && !keyword.isBlank() && category != null && !category.isBlank()) {
            ESpecialization spec = ESpecialization.valueOf(category.toUpperCase());
            posts = forumPostRepository.searchWithFilter(keyword, spec, pageable);
        } else if (keyword != null && !keyword.isBlank()) {
            posts = forumPostRepository.searchByKeyword(keyword, pageable);
        } else if (category != null && !category.isBlank()) {
            ESpecialization spec = ESpecialization.valueOf(category.toUpperCase());
            posts = forumPostRepository.findByCategory(spec, pageable);
        } else {
            posts = forumPostRepository.findAll(pageable);
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

    // ─── Export ───────────────────────────────────────────────────────────────

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
                .isVerifiedAnswer(p.isVerifiedAnswer())
                .viewCount(p.getViewCount())
                .likes(p.getLikes())
                .commentCount(p.getComments() != null ? p.getComments().size() : 0)
                .tags(p.getTags())
                .createdAt(p.getCreatedAt() != null ? p.getCreatedAt().format(FMT) : null)
                .build();
    }

    private String esc(String s) {
        if (s == null) return "";
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
}