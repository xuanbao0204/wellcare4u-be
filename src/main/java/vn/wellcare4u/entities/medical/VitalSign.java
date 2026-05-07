package vn.wellcare4u.entities.medical;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.wellcare4u.entities.Patient;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "vital_sign")
public class VitalSign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private MedicalRecord medicalRecord;
    
    @ManyToOne
    private Patient patient;

    private Double height;
    private Double weight;
    private Double bmi;

    private String bloodPressure;
    private Integer heartRate;
    private Double bloodSugar;

    private LocalDateTime timestamp;
}