package vn.wellcare4u.entities.admin;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import vn.wellcare4u.entities.User;

@AllArgsConstructor
@Data
@Entity
@Table(name = "admin")
public class Admin extends User {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}