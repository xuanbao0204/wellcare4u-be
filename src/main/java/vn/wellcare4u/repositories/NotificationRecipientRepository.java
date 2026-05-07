package vn.wellcare4u.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.NotificationRecipient;

@Repository
public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Long> {

	@Query("""
			SELECT r FROM NotificationRecipient r
			JOIN FETCH r.notification
			WHERE r.user.id = :userId
			ORDER BY r.createdAt DESC
			""")
	List<NotificationRecipient> findByUserId(@Param("userId") Long userId);

	@Query("""
			SELECT COUNT(r) FROM NotificationRecipient r
			WHERE r.user.id = :userId AND r.isRead = false
			""")
	long countUnread(@Param("userId") Long userId);

	@Query("""
			SELECT r FROM NotificationRecipient r
			WHERE r.id = :id AND r.user.id = :userId
			""")
	Optional<NotificationRecipient> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}