package vn.wellcare4u.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.wellcare4u.entities.admin.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long>{

}
