package vn.wellcare4u.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.wellcare4u.entities.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findBySender_IdOrderByCreatedAtDesc(Long userId);
}