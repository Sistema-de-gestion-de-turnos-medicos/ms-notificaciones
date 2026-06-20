package com.universidad.msnotificaciones.client;

import com.universidad.msnotificaciones.dto.MedicoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-medicos", url = "${ms.medicos.url}")
public interface MedicoClient {

    @GetMapping("/api/medicos/{id}")
    MedicoResponseDTO obtenerPorId(@PathVariable Long id);
}
