package vn.wellcare4u.repositories.forum;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.ForumComment;

@Repository
public interface ForumCommentRepository extends JpaRepository<ForumComment, Long> {

	List<ForumComment> findByPostIdAndParentCommentIsNullOrderByCreatedAtAsc(Long postId);

	List<ForumComment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId);

	int countByPostId(Long postId);

	@Query("SELECT COUNT(c) > 0 FROM ForumComment c " + "JOIN Doctor d ON c.author.id = d.id "
			+ "WHERE c.post.id = :postId AND c.isExpertReply = true")
	boolean hasExpertReply(@Param("postId") Long postId);
}
