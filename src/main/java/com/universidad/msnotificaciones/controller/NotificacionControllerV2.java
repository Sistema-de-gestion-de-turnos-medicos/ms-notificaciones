package com.universidad.msnotificaciones.controller;

import com.universidad.msnotificaciones.dto.NotificacionRequestDTO;
import com.universidad.msnotificaciones.dto.NotificacionResponseDTO;
import com.universidad.msnotificaciones.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador documentado de notificaciones.
 * <p>
 * Expone exactamente las mismas operaciones que {@link NotificacionController},
 * pero enriquecidas con anotaciones de OpenAPI/Swagger (resúmenes, ejemplos,
 * esquemas y posibles respuestas) para facilitar su consumo desde Swagger UI.
 */
@RestController
@RequestMapping("/api/v2/notificaciones")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Notificaciones",
        description = "Operaciones para la gestión de notificaciones asociadas a turnos médicos: " +
                "creación, consulta, y cambios de estado (enviada, fallida)."
)
public class NotificacionControllerV2 {

    private final NotificacionService notificacionService;

    @Operation(
            summary = "Listar todas las notificaciones",
            description = "Obtiene el listado completo de notificaciones registradas en el sistema."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Listado de notificaciones obtenido exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = NotificacionResponseDTO.class))
                    )
            )
    })
    @GetMapping
    public List<NotificacionResponseDTO> obtenerTodas() {
        log.info("GET /api/v2/notificaciones - Listar todas las notificaciones");
        return notificacionService.obtenerTodas();
    }

    @Operation(
            summary = "Obtener una notificación por su ID",
            description = "Busca y retorna una notificación específica a partir de su identificador único."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Notificación encontrada",
                    content = @Content(schema = @Schema(implementation = NotificacionResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "No existe una notificación con el ID indicado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> obtenerPorId(
            @Parameter(description = "Identificador único de la notificación", example = "1", required = true)
            @PathVariable Long id) {
        log.info("GET /api/v2/notificaciones/{}", id);
        return notificacionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Listar notificaciones de un paciente",
            description = "Obtiene todas las notificaciones asociadas a un paciente específico."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Notificaciones del paciente obtenidas",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificacionResponseDTO.class)))
            )
    })
    @GetMapping("/paciente/{pacienteId}")
    public List<NotificacionResponseDTO> obtenerPorPaciente(
            @Parameter(description = "Identificador del paciente", example = "5", required = true)
            @PathVariable Long pacienteId) {
        log.info("GET /api/v2/notificaciones/paciente/{}", pacienteId);
        return notificacionService.obtenerPorPaciente(pacienteId);
    }

    @Operation(
            summary = "Listar notificaciones de un turno",
            description = "Obtiene todas las notificaciones asociadas a un turno médico específico."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Notificaciones del turno obtenidas",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificacionResponseDTO.class)))
            )
    })
    @GetMapping("/turno/{turnoId}")
    public List<NotificacionResponseDTO> obtenerPorTurno(
            @Parameter(description = "Identificador del turno médico", example = "10", required = true)
            @PathVariable Long turnoId) {
        log.info("GET /api/v2/notificaciones/turno/{}", turnoId);
        return notificacionService.obtenerPorTurno(turnoId);
    }

    @Operation(
            summary = "Listar notificaciones pendientes",
            description = "Obtiene todas las notificaciones que aún no han sido enviadas " +
                    "(estado PENDIENTE)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Notificaciones pendientes obtenidas",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificacionResponseDTO.class)))
            )
    })
    @GetMapping("/pendientes")
    public List<NotificacionResponseDTO> obtenerPendientes() {
        log.info("GET /api/v2/notificaciones/pendientes");
        return notificacionService.obtenerPendientes();
    }

    @Operation(
            summary = "Registrar una nueva notificación",
            description = "Crea una nueva notificación asociada a un turno, paciente y médico. " +
                    "Queda registrada con estado PENDIENTE hasta que sea enviada."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Notificación creada exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificacionResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "NotificacionCreada",
                                    value = """
                                            {
                                              "id": 1,
                                              "turnoId": 10,
                                              "pacienteId": 5,
                                              "pacienteNombre": "Juan",
                                              "pacienteApellido": "Pérez",
                                              "medicoId": 3,
                                              "medicoNombre": "María",
                                              "medicoApellido": "González",
                                              "tipo": "CONFIRMACION_TURNO",
                                              "estado": "PENDIENTE",
                                              "mensaje": "Su turno ha sido confirmado",
                                              "fechaCreacion": "2026-06-17T10:30:00",
                                              "fechaEnvio": null
                                            }"""
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Datos de la solicitud inválidos (validación fallida)"
            )
    })
    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crear(
            @RequestBody(
                    description = "Datos necesarios para registrar una nueva notificación",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = NotificacionRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "NuevaNotificacion",
                                    value = """
                                            {
                                              "turnoId": 10,
                                              "pacienteId": 5,
                                              "medicoId": 3,
                                              "tipo": "CONFIRMACION_TURNO"
                                            }"""
                            )
                    )
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody NotificacionRequestDTO dto) {
        log.info("POST /api/v2/notificaciones - Crear nueva notificación");
        return ResponseEntity.status(HttpStatus.CREATED).body(notificacionService.crear(dto));
    }

    @Operation(
            summary = "Marcar una notificación como enviada",
            description = "Cambia el estado de una notificación a ENVIADA y registra la fecha de envío."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Notificación marcada como enviada",
                    content = @Content(schema = @Schema(implementation = NotificacionResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "No existe una notificación con el ID indicado"
            )
    })
    @PatchMapping("/{id}/enviada")
    public ResponseEntity<NotificacionResponseDTO> marcarEnviada(
            @Parameter(description = "Identificador único de la notificación", example = "1", required = true)
            @PathVariable Long id) {
        log.info("PATCH /api/v2/notificaciones/{}/enviada", id);
        return notificacionService.marcarComoEnviada(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Marcar una notificación como fallida",
            description = "Cambia el estado de una notificación a FALLIDA, indicando que el " +
                    "envío no pudo completarse."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Notificación marcada como fallida",
                    content = @Content(schema = @Schema(implementation = NotificacionResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "No existe una notificación con el ID indicado"
            )
    })
    @PatchMapping("/{id}/fallida")
    public ResponseEntity<NotificacionResponseDTO> marcarFallida(
            @Parameter(description = "Identificador único de la notificación", example = "1", required = true)
            @PathVariable Long id) {
        log.info("PATCH /api/v2/notificaciones/{}/fallida", id);
        return notificacionService.marcarComoFallida(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Eliminar una notificación",
            description = "Elimina de forma permanente una notificación del sistema."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "Notificación eliminada exitosamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "No existe una notificación con el ID indicado"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador único de la notificación", example = "1", required = true)
            @PathVariable Long id) {
        log.warn("DELETE /api/v2/notificaciones/{}", id);
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
