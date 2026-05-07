package vn.wellcare4u.repositories.forum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.ForumPost;
import vn.wellcare4u.enums.ESpecialization;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {

	Page<ForumPost> findByCategory(ESpecialization category, Pageable pageable);

	@Query("SELECT p FROM ForumPost p WHERE " + "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
			+ "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	Page<ForumPost> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT p FROM ForumPost p WHERE " + "(:category IS NULL OR p.category = :category) AND "
			+ "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR "
			+ " LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	Page<ForumPost> searchWithFilter(@Param("keyword") String keyword, @Param("category") ESpecialization category,
			Pageable pageable);

	Page<ForumPost> findByAuthorId(Long authorId, Pageable pageable);

	Page<ForumPost> findAllByOrderByLikesDesc(Pageable pageable);
}
