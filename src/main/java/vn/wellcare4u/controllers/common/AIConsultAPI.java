package vn.wellcare4u.controllers.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;
import vn.wellcare4u.services.AIService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/common/ai")
public class AIConsultAPI {

    @Autowired
    private AIService aiService;

    @PostMapping("/consult")
    public ResponseEntity<?> consult(@RequestBody Map<String, String> request) {
        String jsonResult = aiService.suggestSpecialty(request.get("symptoms"));
        // Parse chuỗi String JSON thành Object thực sự
        ObjectMapper mapper = new ObjectMapper();
        try {
            Object jsonObject = mapper.readValue(jsonResult, Object.class);
            return ResponseEntity.ok(jsonObject);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}