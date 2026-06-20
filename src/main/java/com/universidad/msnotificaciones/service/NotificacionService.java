package com.universidad.msnotificaciones.service;

import com.universidad.msnotificaciones.client.MedicoClient;
import com.universidad.msnotificaciones.client.PacienteClient;
import com.universidad.msnotificaciones.dto.MedicoResponseDTO;
import com.universidad.msnotificaciones.dto.NotificacionRequestDTO;
import com.universidad.msnotificaciones.dto.NotificacionResponseDTO;
import com.universidad.msnotificaciones.dto.PacienteResponseDTO;
import com.universidad.msnotificaciones.exception.ResourceNotFoundException;
import com.universidad.msnotificaciones.model.EstadoNotificacion;
import com.universidad.msnotificaciones.model.Notificacion;
import com.universidad.msnotificaciones.model.PlantillaNotificacion;
import com.universidad.msnotificaciones.repository.NotificacionRepository;
import com.universidad.msnotificaciones.repository.PlantillaNotificacionRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final PlantillaNotificacionRepository plantillaRepository;
    private final PacienteClient pacienteClient;
    private final MedicoClient medicoClient;

    private NotificacionResponseDTO mapToDTO(Notificacion n,
                                             PacienteResponseDTO paciente,
                                             MedicoResponseDTO medico) {
        return new NotificacionResponseDTO(
                n.getId(),
                n.getTurnoId(),
                n.getPacienteId(),
                paciente != null ? paciente.getNombre() : "No disponible",
                paciente != null ? paciente.getApellido() : "",
                n.getMedicoId(),
                medico != null ? medico.getNombre() : "No disponible",
                medico != null ? medico.getApellido() : "",
                n.getTipo(),
                n.getEstado(),
                n.getMensaje(),
                n.getFechaCreacion(),
                n.getFechaEnvio()
        );
    }

    private PacienteResponseDTO obtenerPaciente(Long pacienteId) {
        try {
            return pacienteClient.obtenerPorId(pacienteId);
        } catch (FeignException e) {
            log.warn("No se pudo obtener paciente con ID {} desde ms-pacientes: {}", pacienteId, e.getMessage());
            return null;
        }
    }

    private MedicoResponseDTO obtenerMedico(Long medicoId) {
        try {
            return medicoClient.obtenerPorId(medicoId);
        } catch (FeignException e) {
            log.warn("No se pudo obtener médico con ID {} desde ms-medicos: {}", medicoId, e.getMessage());
            return null;
        }
    }

    public List<NotificacionResponseDTO> obtenerTodas() {
        log.info("Consultando todas las notificaciones");
        List<NotificacionResponseDTO> resultado = notificacionRepository.findAll().stream()
                .map(n -> mapToDTO(n, obtenerPaciente(n.getPacienteId()), obtenerMedico(n.getMedicoId())))
                .collect(Collectors.toList());
        log.info("Se retornaron {} notificaciones", resultado.size());
        return resultado;
    }

    public Optional<NotificacionResponseDTO> obtenerPorId(Long id) {
        log.info("Buscando notificación con ID: {}", id);
        Optional<NotificacionResponseDTO> resultado = notificacionRepository.findById(id)
                .map(n -> mapToDTO(n, obtenerPaciente(n.getPacienteId()), obtenerMedico(n.getMedicoId())));
        if (resultado.isEmpty()) {
            log.warn("No se encontró notificación con ID: {}", id);
        }
        return resultado;
    }

    public List<NotificacionResponseDTO> obtenerPorPaciente(Long pacienteId) {
        log.info("Consultando notificaciones del paciente ID: {}", pacienteId);
        PacienteResponseDTO paciente = obtenerPaciente(pacienteId);
        List<NotificacionResponseDTO> resultado = notificacionRepository.findByPacienteId(pacienteId).stream()
                .map(n -> mapToDTO(n, paciente, obtenerMedico(n.getMedicoId())))
                .collect(Collectors.toList());
        log.info("Se encontraron {} notificaciones para el paciente ID: {}", resultado.size(), pacienteId);
        return resultado;
    }

    public List<NotificacionResponseDTO> obtenerPorTurno(Long turnoId) {
        log.info("Consultando notificaciones del turno ID: {}", turnoId);
        List<NotificacionResponseDTO> resultado = notificacionRepository.findByTurnoId(turnoId).stream()
                .map(n -> mapToDTO(n, obtenerPaciente(n.getPacienteId()), obtenerMedico(n.getMedicoId())))
                .collect(Collectors.toList());
        log.info("Se encontraron {} notificaciones para el turno ID: {}", resultado.size(), turnoId);
        return resultado;
    }

    public List<NotificacionResponseDTO> obtenerPendientes() {
        log.info("Consultando notificaciones pendientes");
        List<NotificacionResponseDTO> resultado = notificacionRepository
                .findPendientesOrdenadas(EstadoNotificacion.PENDIENTE).stream()
                .map(n -> mapToDTO(n, obtenerPaciente(n.getPacienteId()), obtenerMedico(n.getMedicoId())))
                .collect(Collectors.toList());
        log.info("Se encontraron {} notificaciones pendientes", resultado.size());
        return resultado;
    }

    public NotificacionResponseDTO crear(NotificacionRequestDTO dto) {
        log.info("Creando notificación tipo {} para turno ID: {}, paciente ID: {}",
                dto.getTipo(), dto.getTurnoId(), dto.getPacienteId());

        PacienteResponseDTO paciente = obtenerPaciente(dto.getPacienteId());
        MedicoResponseDTO medico = obtenerMedico(dto.getMedicoId());

        PlantillaNotificacion plantilla = plantillaRepository.findByTipo(dto.getTipo()).orElse(null);

        String mensaje = construirMensaje(dto, paciente, medico, plantilla);

        Notificacion notificacion = new Notificacion(
                null,
                dto.getTurnoId(),
                dto.getPacienteId(),
                dto.getMedicoId(),
                dto.getTipo(),
                EstadoNotificacion.PENDIENTE,
                mensaje,
                LocalDateTime.now(),
                null,
                plantilla
        );

        Notificacion guardada = notificacionRepository.save(notificacion);
        log.info("Notificación creada con ID: {} en estado PENDIENTE", guardada.getId());
        return mapToDTO(guardada, paciente, medico);
    }

    public Optional<NotificacionResponseDTO> marcarComoEnviada(Long id) {
        log.info("Marcando notificación ID: {} como ENVIADA", id);
        Optional<NotificacionResponseDTO> resultado = notificacionRepository.findById(id).map(n -> {
            n.setEstado(EstadoNotificacion.ENVIADA);
            n.setFechaEnvio(LocalDateTime.now());
            Notificacion actualizada = notificacionRepository.save(n);
            log.info("Notificación ID: {} marcada como ENVIADA a las {}", id, actualizada.getFechaEnvio());
            return mapToDTO(actualizada, obtenerPaciente(n.getPacienteId()), obtenerMedico(n.getMedicoId()));
        });
        if (resultado.isEmpty()) {
            log.warn("No se encontró notificación con ID: {} para marcar como enviada", id);
        }
        return resultado;
    }

    public Optional<NotificacionResponseDTO> marcarComoFallida(Long id) {
        log.info("Marcando notificación ID: {} como FALLIDA", id);
        Optional<NotificacionResponseDTO> resultado = notificacionRepository.findById(id).map(n -> {
            n.setEstado(EstadoNotificacion.FALLIDA);
            Notificacion actualizada = notificacionRepository.save(n);
            log.warn("Notificación ID: {} marcada como FALLIDA", id);
            return mapToDTO(actualizada, obtenerPaciente(n.getPacienteId()), obtenerMedico(n.getMedicoId()));
        });
        if (resultado.isEmpty()) {
            log.warn("No se encontró notificación con ID: {} para marcar como fallida", id);
        }
        return resultado;
    }

    public void eliminar(Long id) {
        log.info("Eliminando notificación con ID: {}", id);
        if (!notificacionRepository.existsById(id)) {
            log.warn("Notificación con ID: {} no existe", id);
            throw new ResourceNotFoundException("Notificación no encontrada con ID: " + id);
        }
        notificacionRepository.deleteById(id);
        log.info("Notificación con ID: {} eliminada correctamente", id);
    }

    private String construirMensaje(NotificacionRequestDTO dto,
                                    PacienteResponseDTO paciente,
                                    MedicoResponseDTO medico,
                                    PlantillaNotificacion plantilla) {
        if (plantilla == null) {
            log.warn("No se encontró plantilla para tipo: {}. Usando mensaje genérico.", dto.getTipo());
            return "Notificación de tipo " + dto.getTipo() + " para el turno ID: " + dto.getTurnoId();
        }
        String nombrePaciente = paciente != null ? paciente.getNombre() + " " + paciente.getApellido() : "Paciente";
        String nombreMedico = medico != null ? medico.getNombre() + " " + medico.getApellido() : "Médico";
        return plantilla.getCuerpo()
                .replace("{paciente}", nombrePaciente)
                .replace("{medico}", nombreMedico)
                .replace("{turnoId}", String.valueOf(dto.getTurnoId()));
    }
}
