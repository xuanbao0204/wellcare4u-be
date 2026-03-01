package vn.wellcare4u.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.wellcare4u.entities.Account;
import vn.wellcare4u.entities.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByAccount(Account account);
}
