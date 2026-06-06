package vn.wellcare4u.repositories.forum;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.ForumComment;

@Repository
public interface ForumCommentRepository extends JpaRepository<ForumComment, Long> {

	List<ForumComment> findByPostIdAndParentCommentIsNullOrderByCreatedAtAsc(Long postId);

	List<ForumComment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId);

	int countByPostId(Long postId);

}
