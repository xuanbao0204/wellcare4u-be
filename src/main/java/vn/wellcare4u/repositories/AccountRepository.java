package vn.wellcare4u.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
	public Optional<Account> findByEmail(String email);
	public boolean existsByEmail(String email);
}
