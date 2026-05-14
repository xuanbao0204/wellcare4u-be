package vn.wellcare4u.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.wellcare4u.entities.OtpToken;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findTopByEmailOrderByCreatedAtDesc(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM OtpToken o WHERE o.email = :email")
    void deleteAllByEmail(@Param("email") String email);
}