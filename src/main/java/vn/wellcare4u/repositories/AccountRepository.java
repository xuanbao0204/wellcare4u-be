package vn.wellcare4u.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.Account;
import vn.wellcare4u.enums.EAccountStatus;
import vn.wellcare4u.enums.ERole;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
	public Optional<Account> findByEmail(String email);
	public boolean existsByEmail(String email);
	
	long countByRole(ERole role);

    long countByStatus(EAccountStatus status);

    long countByRoleAndStatus(ERole role, EAccountStatus status);
    
    @Query("""
    	    SELECT DATE(a.createdAt), COUNT(a)
    	    FROM Account a
    	    WHERE a.createdAt >= :from
    	    GROUP BY DATE(a.createdAt)
    	    ORDER BY DATE(a.createdAt)
    	""")
    	List<Object[]> countAccountsGroupedByDate(@Param("from") LocalDateTime from);
    	
    	@Query("""
    	    SELECT DATE_FORMAT(a.createdAt, '%Y-%m'), COUNT(a)
    	    FROM Account a
    	    WHERE a.createdAt >= :from AND a.createdAt < :to
    	    GROUP BY DATE_FORMAT(a.createdAt, '%Y-%m')
    	""")
    	List<Object[]> countAccountsGroupedByMonth(
    	    @Param("from") LocalDateTime from,
    	    @Param("to") LocalDateTime to
    	);

    	@Query("""
    	    SELECT DATE(a.createdAt), COUNT(a)
    	    FROM Account a
    	    WHERE a.createdAt >= :from AND a.createdAt < :to
    	    GROUP BY DATE(a.createdAt)
    	""")
    	List<Object[]> countAccountsGroupedByDate(
    	    @Param("from") LocalDateTime from,
    	    @Param("to") LocalDateTime to
    	);
}
