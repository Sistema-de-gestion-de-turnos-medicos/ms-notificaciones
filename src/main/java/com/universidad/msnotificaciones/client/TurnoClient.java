package com.universidad.msnotificaciones.client;

import com.universidad.msnotificaciones.dto.TurnoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-turnos", url = "${ms.turnos.url}")
public interface TurnoClient {

    @GetMapping("/api/turnos/{id}")
    TurnoResponseDTO obtenerPorId(@PathVariable Long id);
}
