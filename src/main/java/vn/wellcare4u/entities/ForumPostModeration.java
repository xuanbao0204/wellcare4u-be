package vn.wellcare4u.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.wellcare4u.enums.EModerationAction;
import vn.wellcare4u.enums.EModerationSeverity;

@Entity
@Table(name = "forum_post_moderation", indexes = { 
		@Index(name = "idx_moderation_violating", columnList = "violating"),
		@Index(name = "idx_moderation_checked", columnList = "checkedAt") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForumPostModeration {

    @Id
    private Long postId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private ForumPost post;

    private boolean violating;

    @Enumerated(EnumType.STRING)
    private EModerationSeverity severity;

    private Double confidence;

    @Column(columnDefinition = "text")
    private String reason;

    @Enumerated(EnumType.STRING)
    private EModerationAction recommendedAction;

    private boolean medicalEmergency;

    /**
     * JSON Array String
     * ["MEDICAL_MISINFORMATION","SPAM"]
     */
    @Column(length = 2000)
    private String violationCategories;

    /**
     * JSON Array String
     * ["Thuốc này chữa khỏi ung thư 100%","Đừng nghe bác sĩ"]
     */
    @Column(columnDefinition = "text")
    private String violatingContents;

    @Column(columnDefinition = "LONGTEXT")
    private String aiRawResponse;

    private LocalDateTime checkedAt;
}