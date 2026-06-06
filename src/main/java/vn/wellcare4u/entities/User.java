package vn.wellcare4u.entities;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public abstract class User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    private String firstName;
    private String lastName;

    private LocalDate dob;

    private String gender;

    private String phone;
    private String address;
    private String avatar;
    
    @Column(nullable = false)
    private boolean isProfileCompleted = false;

    @OneToOne
    @JoinColumn(name = "account_id", unique = true)
    private Account account;
    
    public String getFullName() {
    	return firstName + " " + lastName;
    }
}
