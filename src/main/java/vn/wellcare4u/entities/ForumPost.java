package vn.wellcare4u.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.wellcare4u.enums.EForumCategory;
import vn.wellcare4u.enums.EPostStatus;
import vn.wellcare4u.enums.ESpecialization;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "forum_post")
public class ForumPost {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	@Column(length = 10000)
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private ESpecialization relatedSpecialization;

	@Enumerated(EnumType.STRING)
	private EForumCategory category;

	@ManyToOne
	@JoinColumn(name = "author_id")
	private User author;

	private boolean isAnonymous;

	private boolean allowComment;

	private long viewCount;

	private long likes;

	private long commentCount;

	@Enumerated(EnumType.STRING)
	private EPostStatus status;

	@ElementCollection
	@CollectionTable(name = "forum_post_tags", joinColumns = @JoinColumn(name = "post_id"))
	@Column(name = "tag")
	private List<String> tags;
	
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = this.updatedAt = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}