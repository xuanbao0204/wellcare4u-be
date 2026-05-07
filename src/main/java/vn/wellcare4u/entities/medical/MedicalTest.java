package vn.wellcare4u.entities.medical;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "medical_test")
public class MedicalTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private MedicalRecord medicalRecord;

    private String testName;

    @Column(length = 2000)
    private String resultText;

    @Column(length = 2000)
    private String conclusion;

    private String imageUrl;

    private LocalDateTime performedAt;
}