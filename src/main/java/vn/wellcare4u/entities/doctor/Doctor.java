package vn.wellcare4u.entities.doctor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.wellcare4u.entities.User;
import vn.wellcare4u.enums.ESpecialization;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
    @Enumerated(EnumType.STRING)
    private ESpecialization specialization;
    private Integer experienceYears;
    private Double consultationFee;
    private String clinicAddress;
    private boolean verified;
}