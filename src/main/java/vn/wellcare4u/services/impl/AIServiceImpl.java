package vn.wellcare4u.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.entities.Patient;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.entities.medical.MedicalRecord;
import vn.wellcare4u.enums.EAppointmentStatus;
import vn.wellcare4u.enums.ESpecialization;
import vn.wellcare4u.services.AIService;

@Service
@Slf4j
public class AIServiceImpl implements AIService {

    @Value("${google.gemini.api.key}")
    private String apiKey;

    @Value("${google.gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String suggestSpecialty(String symptoms) {

        String prompt = """
                Bạn là AI hỗ trợ phân loại chuyên khoa y tế.

                Nhiệm vụ:
                Dựa trên mô tả của bệnh nhân: "%s"

                CHỈ được chọn specialty trong danh sách:
                %s

                Nếu không đủ dữ liệu:
                - specialty = UNKNOWN
                - needMoreInfo = true

                Nếu có dấu hiệu nguy hiểm:
                - specialty = CAP_CUU

                KHÔNG dùng markdown.
                KHÔNG dùng ```json.
                CHỈ trả raw JSON.

                JSON:
                {
                  "specialty": "",
                  "reason": "",
                  "firstAid": "",
                  "needMoreInfo": false
                }
                """.formatted(symptoms, ESpecialization.toPromptString());

        return callGemini(prompt);
    }

    @Override
    public String summarizeMedicalHistory(
            List<MedicalRecord> records,
            Patient patient
    ) {

        if (records == null || records.isEmpty()) {
            return "Chưa có lịch sử khám bệnh.";
        }

        String prompt = buildMedicalSummaryPrompt(records, patient);

        return callGemini(prompt);
    }


    private String callGemini(String prompt) {

        try {

            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));

            Map<String, Object> body = new HashMap<>();
            body.put("contents", List.of(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            String url = apiUrl + "?key=" + apiKey;

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(
                            url,
                            entity,
                            Map.class
                    );

            return extractTextResponse(response.getBody());

        } catch (Exception e) {

            log.error("Gemini API Error", e);

            return "Không thể xử lý AI lúc này.";
        }
    }

    @SuppressWarnings("unchecked")
    private String extractTextResponse(Map<String, Object> body) {

        if (body == null || !body.containsKey("candidates")) {
            return "Không có phản hồi từ AI.";
        }

        try {

            List<Map<String, Object>> candidates =
                    (List<Map<String, Object>>) body.get("candidates");

            if (candidates.isEmpty()) {
                return "AI không trả kết quả.";
            }

            Map<String, Object> content =
                    (Map<String, Object>) candidates.get(0).get("content");

            List<Map<String, Object>> parts =
                    (List<Map<String, Object>>) content.get("parts");

            if (parts.isEmpty()) {
                return "AI không trả nội dung.";
            }

            String text = parts.get(0).get("text").toString();

            return text
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("```", "")
                    .trim();

        } catch (Exception e) {

            log.error("Error parsing Gemini response", e);

            return "Không thể đọc phản hồi AI.";
        }
    }

    private String buildMedicalSummaryPrompt(
            List<MedicalRecord> records,
            Patient patient
    ) {

        StringBuilder sb = new StringBuilder();

        sb.append("""
        	    Bạn là trợ lý AI y tế.

        	    Hãy tạo phần tóm tắt sức khỏe NGẮN GỌN bằng tiếng Việt cho dashboard bệnh nhân.

        	    Yêu cầu:
        	    - Tối đa 400 ký tự
        	    - Viết thành 3-5 ý ngắn
        	    - Không markdown
        	    - Không tiêu đề
        	    - Không mở đầu kiểu "Chào anh..."
        	    - Không giải thích dài dòng
        	    - Không nhắc lại thông tin không quan trọng
        	    - Ưu tiên:
        	      + bệnh nổi bật
        	      + vấn đề tái phát
        	      + tình trạng hiện tại
        	      + khuyến nghị ngắn

        	    Văn phong:
        	    - Chuyên nghiệp
        	    - Ngắn gọn
        	    - Dễ đọc trên dashboard

        	    Ví dụ format mong muốn:

        	    • Huyết áp ổn định trong các lần khám gần đây.
        	    • Từng ghi nhận viêm da cấp tính vào 04/2026.
        	    • Chưa phát hiện bệnh lý tái phát.
        	    • Khuyến nghị duy trì theo dõi và chăm sóc da thường xuyên.

        	    """);

        sb.append(String.format("""
                Bệnh nhân:
                - Họ tên: %s %s
                - Nhóm máu: %s

                """,
                patient.getFirstName(),
                patient.getLastName(),
                patient.getBloodType() != null
                        ? patient.getBloodType()
                        : "Chưa xác định"
        ));

        sb.append("=== LỊCH SỬ KHÁM ===\n\n");

        for (int i = 0; i < Math.min(records.size(), 10); i++) {

            MedicalRecord r = records.get(i);

            sb.append(String.format("""
                    [%d]
                    Ngày khám: %s
                    Triệu chứng: %s
                    Chẩn đoán: %s
                    Điều trị: %s
                    Kết luận: %s

                    """,
                    i + 1,
                    r.getCreatedAt() != null
                            ? r.getCreatedAt().toLocalDate()
                            : "Không rõ",

                    safe(r.getSymptoms()),
                    safe(r.getDiagnosis()),
                    safe(r.getTreatmentPlan()),
                    safe(r.getConclusion())
            ));
        }

        return sb.toString();
    }

    @Override
	public String summarizeDoctorDashboard(
            Doctor doctor,
            List<Appointment> appointments,
            List<MedicalRecord> records
    ) {

        String prompt = buildDoctorDashboardPrompt(
                doctor,
                appointments,
                records
        );

        return callGemini(prompt);
    }
    
    private String buildDoctorDashboardPrompt(
            Doctor doctor,
            List<Appointment> appointments,
            List<MedicalRecord> records
    ) {

        long completed = appointments.stream()
                .filter(a -> a.getStatus() == EAppointmentStatus.COMPLETED)
                .count();

        long cancelled = appointments.stream()
                .filter(a -> a.getStatus() == EAppointmentStatus.CANCELLED)
                .count();

        long pending = appointments.stream()
                .filter(a ->
                        a.getStatus() == EAppointmentStatus.PENDING
                )
                .count();

        StringBuilder sb = new StringBuilder();

        sb.append("""
            Bạn là AI hỗ trợ dashboard bác sĩ.

            Hãy phân tích dữ liệu hoạt động khám bệnh
            và tạo phần INSIGHT NGẮN GỌN bằng tiếng Việt.

            Yêu cầu:
            - tối đa 5 bullet
            - ngắn gọn
            - chuyên nghiệp
            - phù hợp hiển thị dashboard
            - không markdown
            - không giải thích dài
            - tập trung:
              + hiệu suất khám
              + tỉ lệ hủy
              + workload
              + xu hướng bệnh nhân
              + follow-up
              + cảnh báo bất thường

            Format:

            • insight 1
            • insight 2

            """);

        sb.append(String.format("""
            Bác sĩ:
            - Chuyên khoa: %s
            - Kinh nghiệm: %d năm

            Tổng lịch hẹn: %d
            Đã hoàn thành: %d
            Đã hủy: %d
            Đang chờ: %d

            """,
                doctor.getSpecialization().getDisplayName(),
                doctor.getExperienceYears(),
                appointments.size(),
                completed,
                cancelled,
                pending
        ));

        sb.append("=== MEDICAL RECORDS ===\n");

        records.stream()
                .limit(20)
                .forEach(r -> {

                    sb.append(String.format("""
                        Diagnosis: %s
                        Conclusion: %s

                        """,
                            safe(r.getDiagnosis()),
                            safe(r.getConclusion())
                    ));
                });

        return sb.toString();
    }
    
    private String safe(String value) {
        return value != null ? value : "Không có";
    }
}