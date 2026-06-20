package com.universidad.msnotificaciones.service;

import com.universidad.msnotificaciones.dto.PlantillaRequestDTO;
import com.universidad.msnotificaciones.dto.PlantillaResponseDTO;
import com.universidad.msnotificaciones.exception.ResourceNotFoundException;
import com.universidad.msnotificaciones.model.PlantillaNotificacion;
import com.universidad.msnotificaciones.repository.PlantillaNotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlantillaService {

    private final PlantillaNotificacionRepository plantillaRepository;

    private PlantillaResponseDTO mapToDTO(PlantillaNotificacion p) {
        return new PlantillaResponseDTO(p.getId(), p.getTipo(), p.getAsunto(), p.getCuerpo(), p.isActiva());
    }

    public List<PlantillaResponseDTO> obtenerTodas() {
        log.info("Consultando todas las plantillas");
        List<PlantillaResponseDTO> resultado = plantillaRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        log.info("Se retornaron {} plantillas", resultado.size());
        return resultado;
    }

    public List<PlantillaResponseDTO> obtenerActivas() {
        log.info("Consultando plantillas activas");
        List<PlantillaResponseDTO> resultado = plantillaRepository.findByActivaTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        log.info("Se retornaron {} plantillas activas", resultado.size());
        return resultado;
    }

    public Optional<PlantillaResponseDTO> obtenerPorId(Long id) {
        log.info("Buscando plantilla con ID: {}", id);
        Optional<PlantillaResponseDTO> resultado = plantillaRepository.findById(id).map(this::mapToDTO);
        if (resultado.isEmpty()) {
            log.warn("No se encontró plantilla con ID: {}", id);
        }
        return resultado;
    }

    public PlantillaResponseDTO crear(PlantillaRequestDTO dto) {
        log.info("Creando plantilla para tipo: {}", dto.getTipo());
        if (plantillaRepository.findByTipo(dto.getTipo()).isPresent()) {
            log.warn("Ya existe una plantilla para el tipo: {}", dto.getTipo());
            throw new RuntimeException("Ya existe una plantilla para el tipo: " + dto.getTipo());
        }
        PlantillaNotificacion plantilla = new PlantillaNotificacion(
                null, dto.getTipo(), dto.getAsunto(), dto.getCuerpo(), true, null);
        PlantillaNotificacion guardada = plantillaRepository.save(plantilla);
        log.info("Plantilla creada con ID: {} para tipo: {}", guardada.getId(), guardada.getTipo());
        return mapToDTO(guardada);
    }

    public Optional<PlantillaResponseDTO> actualizar(Long id, PlantillaRequestDTO dto) {
        log.info("Actualizando plantilla con ID: {}", id);
        Optional<PlantillaResponseDTO> resultado = plantillaRepository.findById(id).map(existente -> {
            existente.setAsunto(dto.getAsunto());
            existente.setCuerpo(dto.getCuerpo());
            PlantillaNotificacion actualizada = plantillaRepository.save(existente);
            log.info("Plantilla ID: {} actualizada correctamente", id);
            return mapToDTO(actualizada);
        });
        if (resultado.isEmpty()) {
            log.warn("No se encontró plantilla con ID: {} para actualizar", id);
        }
        return resultado;
    }

    public void desactivar(Long id) {
        log.info("Desactivando plantilla con ID: {}", id);
        PlantillaNotificacion plantilla = plantillaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Plantilla con ID: {} no encontrada para desactivar", id);
                    return new ResourceNotFoundException("Plantilla no encontrada con ID: " + id);
                });
        plantilla.setActiva(false);
        plantillaRepository.save(plantilla);
        log.info("Plantilla ID: {} desactivada correctamente", id);
    }
}
