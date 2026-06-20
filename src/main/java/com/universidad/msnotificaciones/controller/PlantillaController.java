package com.universidad.msnotificaciones.controller;

import com.universidad.msnotificaciones.dto.PlantillaRequestDTO;
import com.universidad.msnotificaciones.dto.PlantillaResponseDTO;
import com.universidad.msnotificaciones.service.PlantillaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plantillas")
@RequiredArgsConstructor
public class PlantillaController {

    private final PlantillaService plantillaService;

    @GetMapping
    public List<PlantillaResponseDTO> obtenerTodas() {
        return plantillaService.obtenerTodas();
    }

    @GetMapping("/activas")
    public List<PlantillaResponseDTO> obtenerActivas() {
        return plantillaService.obtenerActivas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantillaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return plantillaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlantillaResponseDTO> crear(@Valid @RequestBody PlantillaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(plantillaService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantillaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PlantillaRequestDTO dto) {
        return plantillaService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        plantillaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
