package com.universidad.msnotificaciones.controller;

import com.universidad.msnotificaciones.dto.NotificacionRequestDTO;
import com.universidad.msnotificaciones.dto.NotificacionResponseDTO;
import com.universidad.msnotificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    public List<NotificacionResponseDTO> obtenerTodas() {
        return notificacionService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return notificacionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/paciente/{pacienteId}")
    public List<NotificacionResponseDTO> obtenerPorPaciente(@PathVariable Long pacienteId) {
        return notificacionService.obtenerPorPaciente(pacienteId);
    }

    @GetMapping("/turno/{turnoId}")
    public List<NotificacionResponseDTO> obtenerPorTurno(@PathVariable Long turnoId) {
        return notificacionService.obtenerPorTurno(turnoId);
    }

    @GetMapping("/pendientes")
    public List<NotificacionResponseDTO> obtenerPendientes() {
        return notificacionService.obtenerPendientes();
    }

    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crear(@Valid @RequestBody NotificacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificacionService.crear(dto));
    }

    @PatchMapping("/{id}/enviada")
    public ResponseEntity<NotificacionResponseDTO> marcarEnviada(@PathVariable Long id) {
        return notificacionService.marcarComoEnviada(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/fallida")
    public ResponseEntity<NotificacionResponseDTO> marcarFallida(@PathVariable Long id) {
        return notificacionService.marcarComoFallida(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
