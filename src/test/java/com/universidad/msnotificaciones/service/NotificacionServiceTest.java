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
import com.universidad.msnotificaciones.model.TipoNotificacion;
import com.universidad.msnotificaciones.repository.NotificacionRepository;
import com.universidad.msnotificaciones.repository.PlantillaNotificacionRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private PlantillaNotificacionRepository plantillaRepository;

    @Mock
    private PacienteClient pacienteClient;

    @Mock
    private MedicoClient medicoClient;

    @InjectMocks
    private NotificacionService notificacionService;

    private Notificacion notificacion;
    private NotificacionRequestDTO requestDTO;
    private PacienteResponseDTO pacienteResponseDTO;
    private MedicoResponseDTO medicoResponseDTO;
    private PlantillaNotificacion plantilla;

    @BeforeEach
    void setUp() {
        notificacion = new Notificacion(
                1L, 10L, 20L, 30L,
                TipoNotificacion.CONFIRMACION_TURNO,
                EstadoNotificacion.PENDIENTE,
                "Notificación de tipo CONFIRMACION_TURNO para el turno ID: 10",
                LocalDateTime.now(),
                null,
                null
        );

        requestDTO = new NotificacionRequestDTO(10L, 20L, 30L, TipoNotificacion.CONFIRMACION_TURNO);

        pacienteResponseDTO = new PacienteResponseDTO(20L, "Ana", "Torres", "ana@correo.com", "987654321", true);
        medicoResponseDTO = new MedicoResponseDTO(30L, "Pedro", "Ramírez", "Pediatría", "M-123", "pedro@correo.com", true);

        plantilla = new PlantillaNotificacion();
        plantilla.setId(1L);
        plantilla.setTipo(TipoNotificacion.CONFIRMACION_TURNO);
        plantilla.setAsunto("Confirmación de turno");
        plantilla.setCuerpo("Hola {paciente}, su turno con {medico} fue confirmado (turno {turnoId}).");
        plantilla.setActiva(true);
    }



    @Test
    void testObtenerTodas() {

        when(notificacionRepository.findAll()).thenReturn(List.of(notificacion));
        when(pacienteClient.obtenerPorId(20L)).thenReturn(pacienteResponseDTO);
        when(medicoClient.obtenerPorId(30L)).thenReturn(medicoResponseDTO);

        List<NotificacionResponseDTO> resultado = notificacionService.obtenerTodas();


        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Ana", resultado.get(0).getPacienteNombre());
        assertEquals("Pedro", resultado.get(0).getMedicoNombre());
    }

    @Test
    void testObtenerTodas_pacienteServiceCaido_usaValorPorDefecto() {
        Request request = Request.create(Request.HttpMethod.GET, "/api/pacientes/20",
                Collections.emptyMap(), null, new RequestTemplate());
        when(notificacionRepository.findAll()).thenReturn(List.of(notificacion));
        when(pacienteClient.obtenerPorId(20L))
                .thenThrow(new FeignException.NotFound("Not Found", request, null, null));
        when(medicoClient.obtenerPorId(30L)).thenReturn(medicoResponseDTO);


        List<NotificacionResponseDTO> resultado = notificacionService.obtenerTodas();

        assertNotNull(resultado);
        assertEquals("No disponible", resultado.get(0).getPacienteNombre());
    }



    @Test
    void testObtenerPorId_exitoso() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(pacienteClient.obtenerPorId(20L)).thenReturn(pacienteResponseDTO);
        when(medicoClient.obtenerPorId(30L)).thenReturn(medicoResponseDTO);

        Optional<NotificacionResponseDTO> resultado = notificacionService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
    }

    @Test
    void testObtenerPorId_noExiste_devuelveOptionalVacio() {
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<NotificacionResponseDTO> resultado = notificacionService.obtenerPorId(99L);


        assertTrue(resultado.isEmpty());
    }

    // ---------- crear ----------

    @Test
    void testCrear_conPlantillaExistente() {
        when(pacienteClient.obtenerPorId(20L)).thenReturn(pacienteResponseDTO);
        when(medicoClient.obtenerPorId(30L)).thenReturn(medicoResponseDTO);
        when(plantillaRepository.findByTipo(TipoNotificacion.CONFIRMACION_TURNO)).thenReturn(Optional.of(plantilla));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);

        NotificacionResponseDTO resultado = notificacionService.crear(requestDTO);


        assertNotNull(resultado);
        assertEquals(EstadoNotificacion.PENDIENTE, resultado.getEstado());
        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }

    @Test
    void testCrear_sinPlantilla_usaMensajeGenerico() {
        when(pacienteClient.obtenerPorId(20L)).thenReturn(pacienteResponseDTO);
        when(medicoClient.obtenerPorId(30L)).thenReturn(medicoResponseDTO);
        when(plantillaRepository.findByTipo(TipoNotificacion.CONFIRMACION_TURNO)).thenReturn(Optional.empty());
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);

        NotificacionResponseDTO resultado = notificacionService.crear(requestDTO);

        assertNotNull(resultado);
        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }



    @Test
    void testMarcarComoEnviada_exitoso() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pacienteClient.obtenerPorId(20L)).thenReturn(pacienteResponseDTO);
        when(medicoClient.obtenerPorId(30L)).thenReturn(medicoResponseDTO);

        Optional<NotificacionResponseDTO> resultado = notificacionService.marcarComoEnviada(1L);

        assertTrue(resultado.isPresent());
        assertEquals(EstadoNotificacion.ENVIADA, resultado.get().getEstado());
        assertNotNull(resultado.get().getFechaEnvio());
    }



    @Test
    void testMarcarComoFallida_exitoso() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pacienteClient.obtenerPorId(20L)).thenReturn(pacienteResponseDTO);
        when(medicoClient.obtenerPorId(30L)).thenReturn(medicoResponseDTO);

        Optional<NotificacionResponseDTO> resultado = notificacionService.marcarComoFallida(1L);

        assertTrue(resultado.isPresent());
        assertEquals(EstadoNotificacion.FALLIDA, resultado.get().getEstado());
    }



    @Test
    void testEliminar_exitoso() {
        when(notificacionRepository.existsById(1L)).thenReturn(true);

        notificacionService.eliminar(1L);

        verify(notificacionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminar_noExiste_lanzaResourceNotFoundException() {
        when(notificacionRepository.existsById(99L)).thenReturn(false);


        assertThrows(ResourceNotFoundException.class, () -> notificacionService.eliminar(99L));
        verify(notificacionRepository, never()).deleteById(any());
    }
}
