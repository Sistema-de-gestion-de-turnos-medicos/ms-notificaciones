package com.universidad.msnotificaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantillaResponseDTO {
    private Long id;
    private String tipo;
    private String asunto;
    private String cuerpo;
    private boolean activa;
}
