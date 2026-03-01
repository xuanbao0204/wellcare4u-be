package vn.wellcare4u.services.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.wellcare4u.entities.Account;
import vn.wellcare4u.entities.RefreshToken;
import vn.wellcare4u.repositories.AccountRepository;
import vn.wellcare4u.repositories.RefreshTokenRepository;
import vn.wellcare4u.services.RefreshTokenService;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final RefreshTokenRepository repo;
    private final AccountRepository accountRepo;

    @Value("${jwt.refresh.expiration.ms}")
    private long refreshExpiration;

    @Override
	public RefreshToken createRefreshToken(Account account) {

        repo.deleteByAccount(account);

        RefreshToken token = new RefreshToken();
        token.setAccount(account);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(refreshExpiration));

        return repo.save(token);
    }

    @Override
	public RefreshToken verify(String token) {

        RefreshToken refreshToken = repo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            repo.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }

    @Override
	public void deleteByAccount(Account account) {
        repo.deleteByAccount(account);
    }
}