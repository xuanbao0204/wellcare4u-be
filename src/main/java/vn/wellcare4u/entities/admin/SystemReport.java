package vn.wellcare4u.entities.admin;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.wellcare4u.entities.User;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "system_report")
public class SystemReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User generatedBy;

    private String type;

    private LocalDateTime generatedAt;

    @Lob
    private String dataSnapshot;
}