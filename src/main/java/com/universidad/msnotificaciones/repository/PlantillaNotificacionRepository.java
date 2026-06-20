package com.universidad.msnotificaciones.repository;

import com.universidad.msnotificaciones.model.PlantillaNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlantillaNotificacionRepository extends JpaRepository<PlantillaNotificacion, Long> {

    Optional<PlantillaNotificacion> findByTipo(String tipo);

    List<PlantillaNotificacion> findByActivaTrue();
}
