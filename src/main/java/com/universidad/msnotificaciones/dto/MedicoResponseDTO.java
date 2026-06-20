package com.universidad.msnotificaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicoResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String especialidad;
    private String matricula;
    private String email;
    private boolean activo;
}
