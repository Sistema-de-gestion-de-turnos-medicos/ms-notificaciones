package com.universidad.msnotificaciones.config;

import com.universidad.msnotificaciones.model.EstadoNotificacion;
import com.universidad.msnotificaciones.model.Notificacion;
import com.universidad.msnotificaciones.model.PlantillaNotificacion;
import com.universidad.msnotificaciones.model.TipoNotificacion;
import com.universidad.msnotificaciones.repository.NotificacionRepository;
import com.universidad.msnotificaciones.repository.PlantillaNotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PlantillaNotificacionRepository plantillaRepository;
    private final NotificacionRepository notificacionRepository;

    @Override
    public void run(String... args) {
        inicializarPlantillas();
        inicializarNotificaciones();
    }

    private void inicializarPlantillas() {
        if (plantillaRepository.count() > 0) {
            log.info("Plantillas ya existentes. Se omite inicialización.");
            return;
        }
        log.info("Cargando plantillas de notificación...");

        plantillaRepository.save(new PlantillaNotificacion(null,
                TipoNotificacion.CONFIRMACION_TURNO,
                "Confirmación de su turno médico",
                "Estimado/a {paciente}, le confirmamos su turno con el Dr/a. {medico} el día {fecha} a las {hora}.",
                true, null));

        plantillaRepository.save(new PlantillaNotificacion(null,
                TipoNotificacion.RECORDATORIO_TURNO,
                "Recordatorio de turno médico",
                "Estimado/a {paciente}, le recordamos que mañana tiene turno con el Dr/a. {medico} a las {hora}.",
                true, null));

        plantillaRepository.save(new PlantillaNotificacion(null,
                TipoNotificacion.CANCELACION_TURNO,
                "Cancelación de su turno médico",
                "Estimado/a {paciente}, le informamos que su turno con el Dr/a. {medico} ha sido cancelado.",
                true, null));

        plantillaRepository.save(new PlantillaNotificacion(null,
                TipoNotificacion.REPROGRAMACION_TURNO,
                "Reprogramación de su turno médico",
                "Estimado/a {paciente}, su turno con el Dr/a. {medico} ha sido reprogramado para el {fecha} a las {hora}.",
                true, null));

        log.info("4 plantillas cargadas correctamente.");
    }

    private void inicializarNotificaciones() {
        if (notificacionRepository.count() > 0) {
            log.info("Notificaciones ya existentes. Se omite inicialización.");
            return;
        }
        log.info("Cargando notificaciones de ejemplo...");

        PlantillaNotificacion plantillaConfirmacion = plantillaRepository
                .findByTipo(TipoNotificacion.CONFIRMACION_TURNO).orElse(null);

        PlantillaNotificacion plantillaRecordatorio = plantillaRepository
                .findByTipo(TipoNotificacion.RECORDATORIO_TURNO).orElse(null);

        notificacionRepository.save(new Notificacion(null, 1L, 1L, 1L,
                TipoNotificacion.CONFIRMACION_TURNO, EstadoNotificacion.ENVIADA,
                "Confirmación de turno generada para paciente 1 con médico 1.",
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2),
                plantillaConfirmacion));

        notificacionRepository.save(new Notificacion(null, 2L, 2L, 1L,
                TipoNotificacion.RECORDATORIO_TURNO, EstadoNotificacion.PENDIENTE,
                "Recordatorio de turno pendiente de envío para paciente 2.",
                LocalDateTime.now(), null,
                plantillaRecordatorio));

        notificacionRepository.save(new Notificacion(null, 3L, 3L, 2L,
                TipoNotificacion.CANCELACION_TURNO, EstadoNotificacion.ENVIADA,
                "Turno cancelado notificado al paciente 3.",
                LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1),
                null));

        log.info("3 notificaciones de ejemplo cargadas correctamente.");
    }
}
