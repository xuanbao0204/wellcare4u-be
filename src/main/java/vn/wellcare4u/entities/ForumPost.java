package vn.wellcare4u.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.wellcare4u.enums.EPostStatus;
import vn.wellcare4u.enums.ESpecialization;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
@Table(name = "forum_post")
public class ForumPost {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 5000)
    private String content;

    @Enumerated(EnumType.STRING)
    private ESpecialization category;
    
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private boolean isAnonymous;
    private boolean isVerifiedAnswer;

    private int viewCount;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<ForumComment> comments;

    private long likes;

    @ElementCollection
    @CollectionTable(name = "forum_post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private List<String> tags;
    
    @Enumerated(EnumType.STRING)
    private EPostStatus status;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}