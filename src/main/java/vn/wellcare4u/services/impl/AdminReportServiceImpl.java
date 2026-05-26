package vn.wellcare4u.services.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import vn.wellcare4u.entities.Account;
import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.entities.User;
import vn.wellcare4u.entities.admin.AuditLog;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.repositories.AccountRepository;
import vn.wellcare4u.repositories.AppointmentRepository;
import vn.wellcare4u.repositories.DoctorRepository;
import vn.wellcare4u.repositories.UserRepository;
import vn.wellcare4u.repositories.AuditLogRepository;
import vn.wellcare4u.repositories.forum.ForumCommentRepository;
import vn.wellcare4u.repositories.forum.ForumPostRepository;
import vn.wellcare4u.services.AdminReportService;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {

    private final AccountRepository accountRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final ForumPostRepository forumPostRepository;
    private final ForumCommentRepository forumCommentRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    public byte[] exportAnalyticsExcel(Long adminUserId) {

        List<Account> accounts = accountRepository.findAll();
        List<Doctor> doctors = doctorRepository.findAll();
        List<Appointment> appointments = appointmentRepository.findAll();

        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {

            createSummarySheet(workbook);

            createAccountsSheet(workbook, accounts);

            createDoctorsSheet(workbook, doctors);

            createAppointmentsSheet(workbook, appointments);

            createForumSheet(workbook);

            workbook.write(outputStream);

            saveAuditLog(adminUserId);

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Export excel failed", e);
        }
    }

    private void createSummarySheet(Workbook workbook) {

        Sheet sheet = workbook.createSheet("Summary");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Metric");
        header.createCell(1).setCellValue("Value");

        createMetricRow(
                sheet,
                1,
                "Total Accounts",
                accountRepository.count()
        );

        createMetricRow(
                sheet,
                2,
                "Total Doctors",
                doctorRepository.count()
        );

        createMetricRow(
                sheet,
                3,
                "Total Appointments",
                appointmentRepository.count()
        );

        createMetricRow(
                sheet,
                4,
                "Total Posts",
                forumPostRepository.count()
        );

        createMetricRow(
                sheet,
                5,
                "Total Comments",
                forumCommentRepository.count()
        );

        autoSize(sheet, 2);
    }

    private void createAccountsSheet(
            Workbook workbook,
            List<Account> accounts
    ) {

        Sheet sheet = workbook.createSheet("Accounts");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Email");
        header.createCell(2).setCellValue("Role");
        header.createCell(3).setCellValue("Status");
        header.createCell(4).setCellValue("Created At");

        int rowIndex = 1;

        for (Account account : accounts) {

            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(account.getId());

            row.createCell(1).setCellValue(
                    safe(account.getEmail())
            );

            row.createCell(2).setCellValue(
                    account.getRole() != null
                            ? account.getRole().name()
                            : ""
            );

            row.createCell(3).setCellValue(
                    account.getStatus() != null
                            ? account.getStatus().name()
                            : ""
            );

            row.createCell(4).setCellValue(
                    account.getCreatedAt() != null
                            ? account.getCreatedAt().toString()
                            : ""
            );
        }

        autoSize(sheet, 5);
    }

    private void createDoctorsSheet(
            Workbook workbook,
            List<Doctor> doctors
    ) {

        Sheet sheet = workbook.createSheet("Doctors");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Full Name");
        header.createCell(2).setCellValue("Verified");
        header.createCell(3).setCellValue("Specialization");
        header.createCell(4).setCellValue("Experience");

        int rowIndex = 1;

        for (Doctor doctor : doctors) {

            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(doctor.getId());

            row.createCell(1).setCellValue(
                    safe(doctor.getFirstName())
                            + " "
                            + safe(doctor.getLastName())
            );

            row.createCell(2).setCellValue(
                    doctor.isVerified()
            );

            row.createCell(3).setCellValue(
                    doctor.getSpecialization() != null
                            ? doctor.getSpecialization().name()
                            : ""
            );

            row.createCell(4).setCellValue(
                    doctor.getExperienceYears() != null
                            ? doctor.getExperienceYears()
                            : 0
            );
        }

        autoSize(sheet, 5);
    }

    private void createAppointmentsSheet(
            Workbook workbook,
            List<Appointment> appointments
    ) {

        Sheet sheet = workbook.createSheet("Appointments");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Patient");
        header.createCell(2).setCellValue("Doctor");
        header.createCell(3).setCellValue("Status");
        header.createCell(4).setCellValue("Created At");

        int rowIndex = 1;

        for (Appointment appointment : appointments) {

            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(
                    appointment.getId()
            );

            row.createCell(1).setCellValue(
                    appointment.getPatient() != null
                            ? appointment.getPatient().getFirstName()
                            : ""
            );

            row.createCell(2).setCellValue(
                    appointment.getDoctor() != null
                            ? appointment.getDoctor().getFirstName()
                            : ""
            );

            row.createCell(3).setCellValue(
                    appointment.getStatus() != null
                            ? appointment.getStatus().name()
                            : ""
            );

            row.createCell(4).setCellValue(
                    appointment.getCreatedAt() != null
                            ? appointment.getCreatedAt().toString()
                            : ""
            );
        }

        autoSize(sheet, 5);
    }

    private void createForumSheet(Workbook workbook) {

        Sheet sheet = workbook.createSheet("Forum");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Total Posts");
        header.createCell(1).setCellValue("Total Comments");

        Row row = sheet.createRow(1);

        row.createCell(0).setCellValue(
                forumPostRepository.count()
        );

        row.createCell(1).setCellValue(
                forumCommentRepository.count()
        );

        autoSize(sheet, 2);
    }

    private void createMetricRow(
            Sheet sheet,
            int rowIndex,
            String metric,
            long value
    ) {

        Row row = sheet.createRow(rowIndex);

        row.createCell(0).setCellValue(metric);

        row.createCell(1).setCellValue(value);
    }

    private void autoSize(Sheet sheet, int totalColumns) {

        for (int i = 0; i < totalColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private String safe(String value) {
        return value != null ? value : "";
    }

    private void saveAuditLog(Long adminUserId) {

        User admin = userRepository.findById(adminUserId)
                .orElseThrow();

        AuditLog log = new AuditLog();

        log.setActor(admin);

        log.setAction("EXPORT_ANALYTICS_EXCEL");

        log.setEntityType("REPORT");

        log.setEntityId(null);

        log.setTimestamp(LocalDateTime.now());

        auditLogRepository.save(log);
    }
}