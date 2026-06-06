package vn.wellcare4u.utils;

import java.util.List;
import java.util.stream.Collectors;

import vn.wellcare4u.entities.Appointment;
import vn.wellcare4u.entities.ForumPost;
import vn.wellcare4u.entities.Patient;
import vn.wellcare4u.entities.doctor.Doctor;
import vn.wellcare4u.entities.medical.MedicalRecord;
import vn.wellcare4u.enums.EAppointmentStatus;
import vn.wellcare4u.enums.EModerationAction;
import vn.wellcare4u.enums.EModerationSeverity;
import vn.wellcare4u.enums.ESpecialization;

public class AIPromptBuilder {

	public static String PatientMedicalSummaryPrompt(List<MedicalRecord> records, Patient patient) {
		String medicalData = records.stream().map(MedicalRecord::infoForSummary).collect(Collectors.joining("\n"));

		return String.format("""
				   Bạn là AI Medical Summary Assistant.

				Nhiệm vụ:
				Tổng hợp hồ sơ khám bệnh thành bản tóm tắt y khoa NGẮN GỌN.

				Thông tin bệnh nhân:
				- Họ tên: %s

				Medical Records:
				%s

				QUY TẮC:

				1. Chỉ sử dụng thông tin có trong Medical Records
				2. Không suy diễn hoặc bịa thêm thông tin
				3. Nếu có nhiều records, ưu tiên record mới nhất và nhận diện diễn tiến điều trị
				4. Tóm tắt theo ĐÚNG format dưới đây
				5. Không thêm markdown hoặc giải thích ngoài format

				FORMAT:

				Tình hình bệnh:
				- Tóm tắt dựa trên Conclusion của các lần khám
				- Nếu có diễn tiến thì mô tả ngắn gọn

				Phác đồ điều trị:
				- Tóm tắt từ TreatmentPlan
				- Nêu hướng điều trị hiện tại

				Tái khám:
				- Cho biết có follow-up hay không
				- Nếu có, nêu lịch tái khám gần nhất

				Hồ sơ gần nhất:
				- Diagnosis
				- Conclusion
				- CreatedAt của record cuối

				Chỉ trả về nội dung summary.
				   """, patient.getFullName(), medicalData);
	}

	public static String DoctorDashboardSummaryPrompt(Doctor doctor, List<Appointment> appointments,
			List<MedicalRecord> records) {

		long completed = appointments.stream().filter(a -> a.getStatus() == EAppointmentStatus.COMPLETED).count();

		long cancelled = appointments.stream().filter(a -> a.getStatus() == EAppointmentStatus.CANCELLED).count();

		long pending = appointments.stream().filter(a -> a.getStatus() == EAppointmentStatus.PENDING).count();

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

				""", doctor.getSpecialization().getDisplayName(), doctor.getExperienceYears(), appointments.size(),
				completed, cancelled, pending));

		sb.append("=== MEDICAL RECORDS ===\n");

		records.stream().limit(20).forEach(r -> {

			sb.append(String.format("""
					Diagnosis: %s
					Conclusion: %s

					""", r.getDiagnosis(), r.getConclusion()));
		});

		return sb.toString();
	}

	public static String RecommendSpeciality(String userMessage) {
		return String.format(
				"""
						Bạn là AI Healthcare Assistant hỗ trợ định hướng chuyên khoa khám bệnh.

						NHIỆM VỤ:
						Phân tích triệu chứng hoặc mô tả sức khỏe từ người dùng và đề xuất chuyên khoa khám phù hợp nhất.

						Triệu chứng bệnh nhân cung cấp là:

						"%s"

						DANH SÁCH CHUYÊN KHOA HỢP LỆ:
						%s

						QUY TẮC BẮT BUỘC:

						Chỉ được chọn chuyên khoa từ danh sách trên.
						Không tự tạo chuyên khoa mới hoặc dùng tên khác.
						Chỉ dựa trên thông tin người dùng cung cấp.
						Không suy diễn bệnh lý, không giả định triệu chứng chưa được đề cập.
						Không chẩn đoán bệnh.
						Nhiệm vụ của bạn chỉ là định hướng chuyên khoa phù hợp.
						Ưu tiên triệu chứng chính và vị trí cơ thể được người dùng mô tả.

						Ví dụ:

						Ngứa da, nổi mẩn, bong tróc → Da liễu
						Đau ngực, khó thở → Tim mạch hoặc Hô hấp tùy mô tả
						Đau bụng, tiêu hóa → Tiêu hóa
						Mất ngủ, lo âu → Tâm thần hoặc Thần kinh tùy ngữ cảnh
						Nếu thông tin chưa đủ rõ để xác định chắc chắn:
						Chọn chuyên khoa có khả năng phù hợp nhất
						confidence phải thấp
						ghi rõ cần hỏi thêm
						Nếu người dùng nêu rõ mong muốn khám chuyên khoa cụ thể và không mâu thuẫn với triệu chứng:
						ưu tiên chuyên khoa người dùng yêu cầu.
						Nếu có nhiều chuyên khoa khả thi:
						chọn MỘT chuyên khoa phù hợp nhất
						không trả về danh sách nhiều chuyên khoa.

						ĐỊNH DẠNG OUTPUT: là một JSON OBJECT có format:
						{
							"userSymptom",
							"suggestion",
							"message",
							"matchingRate",
						}

						với:
						- userSymptom: là triệu chứng do người dùng nhập
						- suggestion: là chuyên khoa mà bạn đề xuất dự trên userSymptom
						- matchingRate: độ chính xác ước lượng của chuyên khoa đề xuất, cái này bạn dựa trên độ matching của userSymptom và suggestion

						ví dụ:

						{
							"userSymptom":"Tôi hay bị đau ngực, thỉnh thoảng nhói trong tim",
							"suggestion":"Tim mạch",
							"message":"Đây là chuyên khoa mà tôi đề xuất dựa trên các triệu chứng mà bạn cung cấp. Hãy cân nhắc khi sử dụng nhé. Nếu muốn thêm triệu chứng, bạn có thể nhập và tôi sẽ đề xuất lại cho bạn."
							"matchingRate":"0.9",
						}

						LƯU Ý:
						Nếu thông tin người dùng đưa ra chưa đủ hay chưa rõ ràng, chưa thể thành context để đề xuất, ví dụ "Tôi bị đau", "Tôi hay bị mỏi",...
						thì trả về message với suggestion = null.
						ví dụ:
						user: Tôi bị đau
						trả về:
						{
							"userSymptom":"Tôi bị đau",
							"suggestion": null,
							"message":"Tôi thấy rằng chưa đủ dữ liệu để đề xuất, bạn hãy thử nêu chi tiết hơn đi."
							"matchingRate":"0.0",
						}

						ví dụ:

						{
							"userSymptom":"Tôi hay bị đau ngực, thỉnh thoảng nhói trong tim",
							"suggestion":"Tim mạch",
							"message":"Đây là chuyên khoa mà tôi đề xuất dựa trên các triệu chứng mà bạn cung cấp. Hãy cân nhắc khi sử dụng nhé. Nếu muốn thêm triệu chứng, bạn có thể nhập và tôi sẽ đề xuất lại cho bạn."
							"matchingRate":"0.9",
						}

						""",
				userMessage, ESpecialization.toLabelsString());
	}

	public static String CheckViolation(ForumPost p) {
		return """
				Bạn là AI Moderation Engine của nền tảng chăm sóc sức khỏe.

				Nhiệm vụ:
				Phân tích bài viết và xác định xem nội dung có vi phạm chính sách cộng đồng hoặc gây nguy hiểm cho người dùng hay không.

				=========================
				THÔNG TIN BÀI VIẾT
				=========================

				Title:
				%s

				Content:
				%s

				Tags:
				%s

				=========================
				QUY TẮC ĐÁNH GIÁ
				=========================

				Đánh dấu vi phạm nếu phát hiện:

				1. Ngôn từ thù ghét
				- Kỳ thị dân tộc
				- Kỳ thị giới tính
				- Kỳ thị tôn giáo
				- Kích động bạo lực

				2. Quấy rối hoặc xúc phạm cá nhân
				- Chửi bới
				- Lăng mạ
				- Đe dọa

				3. Nội dung tình dục không phù hợp

				4. Nội dung chính trị cực đoan

				5. Quảng cáo, spam

				6. Thông tin y tế nguy hiểm
				Ví dụ:
				- Khuyên tự tử
				- Khuyên tự gây thương tích
				- Khuyên ngừng thuốc bác sĩ kê đơn
				- Khuyến khích sử dụng chất cấm
				- Chia sẻ phương pháp điều trị nguy hiểm không có cơ sở

				7. Thông tin y khoa sai lệch nghiêm trọng
				Ví dụ:
				- Ung thư có thể chữa khỏi bằng nước muối
				- HIV có thể tự khỏi
				- Vaccine gây vô sinh

				=========================
				MỨC ĐỘ NGHIÊM TRỌNG
				=========================

				SAFE
				- Không phát hiện vi phạm.

				MINOR
				- Nội dung gây khó chịu nhẹ.
				- Spam nhẹ.
				- Ngôn từ thiếu lịch sự.

				MODERATE
				- Công kích cá nhân.
				- Thông tin y khoa đáng nghi.
				- Quảng cáo mạnh.
				- Nội dung gây tranh cãi.

				SEVERE
				- Kích động tự tử.
				- Kích động bạo lực.
				- Nội dung thù ghét.
				- Thông tin y tế cực kỳ nguy hiểm.
				- Nội dung có thể gây hại trực tiếp đến sức khỏe hoặc tính mạng.

				=========================
				MEDICAL EMERGENCY
				=========================

				medicalEmergency = true nếu nội dung đề cập:

				- Tự tử
				- Tự gây thương tích
				- Đau ngực dữ dội
				- Khó thở nghiêm trọng
				- Đột quỵ
				- Mất ý thức
				- Chảy máu không kiểm soát
				- Co giật
				- Ngộ độc
				- Các tình trạng có nguy cơ đe dọa tính mạng

				Ngược lại trả về false.

				=========================
				ENUM GIÁ TRỊ HỢP LỆ
				=========================

				severity phải là một trong:
				%s

				recommendedAction phải là một trong:
				%s
				
				=========================
				VIOLATION CATEGORIES
				=========================
				
				categories ở đây là violation category, chỉ được phép chứa các giá trị sau:
				
				- HATE_SPEECH
				- HARASSMENT
				- SEXUAL_CONTENT
				- EXTREMIST_POLITICS
				- SPAM
				- MEDICAL_DANGER
				- MEDICAL_MISINFORMATION
				
				Không được tạo giá trị khác.
				
				Nếu không phát hiện vi phạm:
				
				categories = []
				
				=========================
				YÊU CẦU JSON
				=========================

				Trả về DUY NHẤT một JSON hợp lệ.

				Schema:

				{
				  "isViolating": boolean,
				  "severity": string,
				  "confidence": number,
				  "reason": string,
				  "violatingContent": string[],
				  "categories": string[],
				  "recommendedAction": string,
				  "medicalEmergency": boolean
				}

				Quy tắc:

				- confidence từ 0.0 đến 1.0
				- violatingContent chứa chính xác các đoạn vi phạm
				- categories là danh sách nhãn ngắn gọn
				- Nếu không vi phạm:
				  - isViolating = false
				  - severity = "SAFE"
				  - violatingContent = []
				  - categories = []
				- Không được tạo field ngoài schema
				- Không được giải thích
				- Không được bọc markdown
				- Không được thêm text ngoài JSON

				JSON:
				"""
				.formatted(
				    p.getTitle(),
				    p.getContent(),
				    p.getTags(),
				    EModerationSeverity.getPromptString(),
				    EModerationAction.getPromptString()
				);
	}
}