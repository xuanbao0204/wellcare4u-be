package vn.wellcare4u.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "patient")
public class Patient extends User {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String emergencyContact;
    private String bloodType;
    private String insuranceNumber;
}
