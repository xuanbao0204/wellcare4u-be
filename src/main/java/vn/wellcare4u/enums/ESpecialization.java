package vn.wellcare4u.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ESpecialization {
	TIM_MACH("Tim mạch"),
    DA_LIEU("Da liễu"),
    TIEU_HOA_GAN_MAT("Tiêu hóa - Gan mật"),
    THAN_KINH("Thần kinh"),
    NOI_TIET("Nội tiết"),
    HO_HAP("Hô hấp"),
    THAN_TIET_NIEU("Thận - Tiết niệu"),
    CO_XUONG_KHOP("Cơ xương khớp"),
    HUYET_HOC("Huyết học"),
    TRUYEN_NHIEM("Truyền nhiễm"),
    NOI_TONG_QUAT("Nội tổng quát"),
    NGOAI_TONG_QUAT("Ngoại tổng quát"),
    NGOAI_THAN_KINH("Ngoại thần kinh"),
    CHAN_THUONG_CHINH_HINH("Chấn thương chỉnh hình"),
    NGOAI_LONG_NGUC_TIM_MACH("Ngoại lồng ngực - Tim mạch"),
    NAM_KHOA("Nam khoa"),
    PHAU_THUAT_THAM_MY("Phẫu thuật thẩm mỹ"),
    SAN_PHU_KHOA("Sản phụ khoa"),
    NHI_KHOA("Nhi khoa"),
    TAI_MUI_HONG("Tai Mũi Họng"),
    RANG_HAM_MAT("Răng Hàm Mặt"),
    NHAN_KHOA("Nhãn khoa"),
    SUC_KHOE_TAM_THAN("Sức khỏe tâm thần"),
    UNG_BUOU("Ung bướu"),
    CHAN_DOAN_HINH_ANH("Chẩn đoán hình ảnh"),
    XET_NGHIEM("Xét nghiệm"),
    GAY_ME_HOI_SUC("Gây mê hồi sức"),
    PHUC_HOI_CHUC_NANG("Phục hồi chức năng"),
    DINH_DUONG("Dinh dưỡng"),
    Y_HOC_CO_TRUYEN("Y học cổ truyền"),
    Y_HOC_GIA_DINH("Y học gia đình"),
    CAP_CUU("Cấp cứu"),
    LAO_KHOA("Lão khoa");

    private final String displayName;

    ESpecialization(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public static String toPromptString() {
    	return Arrays.stream(ESpecialization.values())
    			.map(Enum::name)
    			.collect(Collectors.joining(", "));
    }
}
