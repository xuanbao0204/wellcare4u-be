package vn.wellcare4u.enums;

import lombok.Getter;

@Getter
public enum EForumCategory {

    QANDA("Q&A", "Hỏi đáp cùng bác sĩ"),
    FAQ("FAQ", "Câu hỏi thường gặp"),

    MEDICAL_KNOWLEDGE("KNOWLEDGE", "Kiến thức y khoa"),
    NUTRITION_LIFESTYLE("NUTRITION", "Dinh dưỡng & Sống khỏe"),
    MEDICINE_GUIDE("MEDICINE", "Hướng dẫn sử dụng thuốc"),

    HEALTH_NEWS("NEWS", "Tin tức y tế & Sự kiện"),
    HOSPITAL_GUIDE("GUIDE", "Hướng dẫn đặt lịch & Khám bệnh"),
    PATIENT_STORY("STORY", "Góc chia sẻ & Câu chuyện người bệnh");

    private final String code;
    private final String value;

    EForumCategory(String code, String value) {
        this.code = code;
        this.value = value;
    }
}
