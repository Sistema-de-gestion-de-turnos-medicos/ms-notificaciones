package com.universidad.msnotificaciones.client;

import com.universidad.msnotificaciones.dto.PacienteResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-pacientes", url = "${ms.pacientes.url}")
public interface PacienteClient {

    @GetMapping("/api/pacientes/{id}")
    PacienteResponseDTO obtenerPorId(@PathVariable Long id);
}
