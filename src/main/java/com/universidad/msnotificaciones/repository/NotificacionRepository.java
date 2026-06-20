package com.universidad.msnotificaciones.repository;

import com.universidad.msnotificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByPacienteId(Long pacienteId);

    List<Notificacion> findByTurnoId(Long turnoId);

    List<Notificacion> findByEstado(String estado);

    List<Notificacion> findByMedicoId(Long medicoId);

    @Query("SELECT n FROM Notificacion n WHERE n.pacienteId = :pacienteId AND n.tipo = :tipo")
    List<Notificacion> findByPacienteIdAndTipo(@Param("pacienteId") Long pacienteId,
                                               @Param("tipo") String tipo);

    @Query("SELECT n FROM Notificacion n WHERE n.estado = :estado ORDER BY n.fechaCreacion ASC")
    List<Notificacion> findPendientesOrdenadas(@Param("estado") String estado);
}
