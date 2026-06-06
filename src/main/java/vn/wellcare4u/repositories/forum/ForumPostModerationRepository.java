package vn.wellcare4u.repositories.forum;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.wellcare4u.entities.ForumPost;
import vn.wellcare4u.entities.ForumPostModeration;
import vn.wellcare4u.enums.EModerationSeverity;

public interface ForumPostModerationRepository extends JpaRepository<ForumPostModeration, Long> {

	boolean existsByPostId(Long postId);

	List<ForumPostModeration> findByViolatingTrue();

	List<ForumPostModeration> findBySeverity(EModerationSeverity severity);
	
	ForumPostModeration findByPost(ForumPost post);
}