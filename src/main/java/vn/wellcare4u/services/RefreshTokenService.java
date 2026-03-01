package vn.wellcare4u.services;

import vn.wellcare4u.entities.Account;
import vn.wellcare4u.entities.RefreshToken;

public interface RefreshTokenService {

	void deleteByAccount(Account account);

	RefreshToken verify(String token);

	RefreshToken createRefreshToken(Account account);

}
