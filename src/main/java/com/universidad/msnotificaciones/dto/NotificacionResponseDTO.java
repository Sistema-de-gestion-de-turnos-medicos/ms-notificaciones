package com.universidad.msnotificaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionResponseDTO {
    private Long id;
    private Long turnoId;
    private Long pacienteId;
    private String pacienteNombre;
    private String pacienteApellido;
    private Long medicoId;
    private String medicoNombre;
    private String medicoApellido;
    private String tipo;
    private String estado;
    private String mensaje;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEnvio;
}
