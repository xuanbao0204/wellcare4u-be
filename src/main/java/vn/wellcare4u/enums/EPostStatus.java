package vn.wellcare4u.enums;

import lombok.Getter;

@Getter
public enum EPostStatus {

    PENDING_REVIEW("Đang chờ kiểm duyệt"),

    PUBLISHED("Đã đăng"),

    HIDDEN("Đã ẩn"),

    LOCKED("Đã khóa"),

    DELETED("Đã xóa");

    private final String value;

    EPostStatus(String value) {
        this.value = value;
    }
}