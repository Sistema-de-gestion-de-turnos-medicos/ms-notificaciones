package com.universidad.msnotificaciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionRequestDTO {

    @NotNull(message = "El turnoId es obligatorio")
    private Long turnoId;

    @NotNull(message = "El pacienteId es obligatorio")
    private Long pacienteId;

    @NotNull(message = "El medicoId es obligatorio")
    private Long medicoId;

    @NotBlank(message = "El tipo de notificación es obligatorio")
    private String tipo;
}
