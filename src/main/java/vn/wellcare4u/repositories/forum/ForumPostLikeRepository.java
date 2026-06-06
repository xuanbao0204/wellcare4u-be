package vn.wellcare4u.repositories.forum;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.wellcare4u.entities.ForumPostLike;

public interface ForumPostLikeRepository
    extends JpaRepository<ForumPostLike, Long> {

    boolean existsByPost_IdAndUser_Id(
        Long postId,
        Long userId
    );

    Optional<ForumPostLike> findByPost_IdAndUser_Id(
        Long postId,
        Long userId
    );

    long countByPost_Id(Long postId);
}