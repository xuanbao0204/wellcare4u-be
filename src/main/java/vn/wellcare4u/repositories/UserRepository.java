package vn.wellcare4u.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.User;
import vn.wellcare4u.enums.ERole;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByAccount_Id(Long accountId);
	Optional<User> findByAccount_Email(String email);
	List<User> findAllByAccount_Role(ERole role);
	long countByAccount_Role(ERole role);
	
	@Query("""
			SELECT u from User u
			WHERE u.account.role <> :role
			""")
	List<User> findAllExcept(@Param("role") ERole role);
}
