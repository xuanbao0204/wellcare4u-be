package vn.wellcare4u.repositories.forum;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.ForumPost;
import vn.wellcare4u.enums.EForumCategory;
import vn.wellcare4u.enums.EPostStatus;
import vn.wellcare4u.enums.ESpecialization;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {

	Page<ForumPost> findByRelatedSpecialization(ESpecialization category, Pageable pageable);
	
	Page<ForumPost> findByCategory(EForumCategory category, Pageable pageable);

	@Query("""
			SELECT p FROM ForumPost p 
			WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
			OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))	
			AND p.status = PUBLISHED
			""")
	Page<ForumPost> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

	@Query("""
			SELECT p
			FROM ForumPost p
			WHERE (:category IS NULL OR p.category = :category)
			AND (:specialization IS NULL
			    OR p.relatedSpecialization = :specialization)
			AND	(LOWER(p.title)	LIKE LOWER(CONCAT('%', :keyword, '%'))
			OR LOWER(p.content)	LIKE LOWER(CONCAT('%', :keyword, '%')))
			AND p.status = PUBLISHED
			""")
	Page<ForumPost> searchWithFilter(
			@Param("keyword") String keyword,
			@Param("category") EForumCategory category,
			@Param("specialization") ESpecialization specialization,
			Pageable pageable
	);
	
	@Query("""
			SELECT p
			FROM ForumPost p
			WHERE p.author.id = :userid
			AND (:category IS NULL OR p.category = :category)
			AND (:specialization IS NULL
			    OR p.relatedSpecialization = :specialization)
			AND	(LOWER(p.title)	LIKE LOWER(CONCAT('%', :keyword, '%'))
			OR LOWER(p.content)	LIKE LOWER(CONCAT('%', :keyword, '%')))
			AND p.status = PUBLISHED
			""")
	Page<ForumPost> searchWithFilterAndUserId(
			@Param("keyword") String keyword,
			@Param("category") EForumCategory category,
			@Param("specialization") ESpecialization specialization,
			@Param("userid") Long userid,
			Pageable pageable
	);

	Page<ForumPost> findByAuthorId(Long authorId, Pageable pageable);

	Page<ForumPost> findAllByOrderByLikesDesc(Pageable pageable);
	
	List<ForumPost> findByStatus(EPostStatus status);
	
	@Query("""
			SELECT p
			FROM ForumPost p
			WHERE
			(:category IS NULL OR p.category = :category)
			AND
			(:specialization IS NULL
			    OR p.relatedSpecialization = :specialization)
			""")
	Page<ForumPost> filterPosts(EForumCategory category, ESpecialization specialization, Pageable pageable);
	
	@Query("""
			SELECT p
			FROM ForumPost p
			WHERE p.author.id = :userid
			AND
			(:category IS NULL OR p.category = :category)
			AND
			(:specialization IS NULL
			    OR p.relatedSpecialization = :specialization)
			""")
	Page<ForumPost> filterPostsWithUserId(@Param("category") EForumCategory category,@Param("specialization") ESpecialization specialization, @Param("userid") Long userid,  Pageable pageable);
}
