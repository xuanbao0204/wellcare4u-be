package vn.wellcare4u.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vn.wellcare4u.entities.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
//
//    List<Notification> findByReceiver_IdOrderByCreatedAtDesc(Long userId);
//
//    @Query("""
//        SELECT COUNT(n)
//        FROM Notification n
//        WHERE n.receiver.id = :userId AND n.isRead = false
//    """)
//    long countUnread(Long userId);
}