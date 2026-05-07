package vn.wellcare4u.models.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExceptionRequest {

    private Long doctorId;

    @NotNull
    private LocalDate date;

    private String reason;
}