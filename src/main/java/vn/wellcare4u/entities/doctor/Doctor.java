package vn.wellcare4u.entities.doctor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.wellcare4u.entities.User;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "doctor")
public class Doctor extends User {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(length = 2000)
    private String bio;

    private String certification;
    private String specialization;
    private Integer experienceYears;
    private Double consultationFee;
    private String clinicAddress;
    private boolean verified;
}