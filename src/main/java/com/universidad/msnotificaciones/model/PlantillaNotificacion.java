package com.universidad.msnotificaciones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plantillas_notificacion")
public class PlantillaNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo", nullable = false, unique = true, length = 30)
    private String tipo;

    @Column(name = "asunto", nullable = false, length = 200)
    private String asunto;

    @Column(name = "cuerpo", nullable = false, length = 1000)
    private String cuerpo;

    @Column(name = "activa", nullable = false)
    private boolean activa = true;

    @OneToMany(mappedBy = "plantilla", fetch = FetchType.LAZY)
    private List<Notificacion> notificaciones;
}
