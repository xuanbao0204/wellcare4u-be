package vn.wellcare4u.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import vn.wellcare4u.services.AIService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIServiceImpl implements AIService {

    @Value("${google.gemini.api.key}")
    private String apiKey;

    @Value("${google.gemini.api.url}")
    private String apiUrl;

    @Override
    public String suggestSpecialty(String symptoms) {
        RestTemplate restTemplate = new RestTemplate();

        String prompt = """
            Bạn là một hệ thống hỗ trợ phân loại y tế thông minh.
            
            Nhiệm vụ:
            Dựa trên mô tả của bệnh nhân: "%s", hãy thực hiện phân tích:
            1. Nếu mô tả quá ngắn hoặc mơ hồ (ví dụ: 'tôi đau', 'giúp tôi'), hãy yêu cầu thêm thông tin.
            2. Nếu thông tin đã đủ rõ ràng, hãy gợi ý chuyên khoa phù hợp nhất.
            
            BẮT BUỘC trả về kết quả dưới dạng JSON theo cấu trúc sau (không viết gì ngoài JSON):
            {
              "specialty": "Mã chuyên khoa (ví dụ: TIM_MACH, SUC_KHOE_TAM_THAN...) hoặc 'UNKNOWN' nếu cần hỏi thêm",
              "reason": "Giải thích ngắn gọn tại sao chọn chuyên khoa này dựa trên triệu chứng",
              "firstAid": "Gợi ý lưu ý an toàn tạm thời trước khi đến khám (nếu có)",
              "needMoreInfo": true/false (true nếu thông tin bệnh nhân cung cấp chưa đủ để kết luận)
            }
            
            Danh sách chuyên khoa: [TIM_MACH, DA_LIEU, TIEU_HOA_GAN_MAT, THAN_KINH, NOI_TIET, HO_HAP, THAN_TIET_NIEU, CO_XUONG_KHOP, HUYET_HOC, TRUYEN_NHIEM, NOI_TONG_QUAT, NGOAI_TONG_QUAT, SAN_PHU_KHOA, NHI_KHOA, TAI_MUI_HONG, RANG_HAM_MAT, NHAN_KHOA, SUC_KHOE_TAM_THAN, UNG_BUOU, CAP_CUU].
            """.formatted(symptoms);

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> parts = new HashMap<>();
        parts.put("text", prompt);

        Map<String, Object> contents = new HashMap<>();
        contents.put("parts", List.of(parts));
        requestBody.put("contents", List.of(contents));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            String urlWithKey = apiUrl + "?key=" + apiKey;

            // 1. In thử xem đã lấy được API Key chưa
            System.out.println("--- BẮT ĐẦU GỌI GEMINI ---");
            System.out.println("Prompt: " + prompt);

            ResponseEntity<Map> response = restTemplate.postForEntity(urlWithKey, entity, Map.class);

            // 2. In toàn bộ dữ liệu Google trả về xem có gì bên trong
            Map<String, Object> body = response.getBody();
            System.out.println("Kết quả từ Google Gemini: " + body);

            if (body != null && body.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> responseParts = (List<Map<String, Object>>) content.get("parts");

                    // 1. Lấy chuỗi JSON thô từ AI
                    String rawJson = responseParts.get(0).get("text").toString().trim();

                    // 2. Làm sạch chuỗi (xóa các ký tự đánh dấu markdown ```json)
                    String cleanJson = rawJson.replaceAll("(?s)```json\\s*|```", "").trim();

                    System.out.println("✅ AI Response JSON: " + cleanJson);
                    return cleanJson; // Bây giờ trả về là một chuỗi JSON string
                }
            }
            System.out.println("⚠️ API chạy thành công nhưng không có kết quả trả về.");
            return "GENERAL";

        } catch (Exception e) {
            // 3. In toàn bộ chi tiết lỗi đỏ chót ra để dễ tìm
            System.err.println("❌ LỖI KHI GỌI GEMINI API:");
            e.printStackTrace();
            return "GENERAL";
        }
    }
}