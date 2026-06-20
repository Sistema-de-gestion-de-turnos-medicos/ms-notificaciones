package com.universidad.msnotificaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoResponseDTO {
    private Long id;
    private Long pacienteId;
    private Long medicoId;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;
}
