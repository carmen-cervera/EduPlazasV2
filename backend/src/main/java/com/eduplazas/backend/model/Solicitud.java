package com.eduplazas.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "solicitudes")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaPresentacion;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitudEnum estado;

    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "convocatoria_id")
    private Convocatoria convocatoria;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL)
    private List<Preferencia> preferencias;

    @OneToOne(mappedBy = "solicitud")
    @JsonIgnore
    private Asignacion asignacion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFechaPresentacion() { return fechaPresentacion; }
    public void setFechaPresentacion(LocalDate fechaPresentacion) { this.fechaPresentacion = fechaPresentacion; }
    public EstadoSolicitudEnum getEstado() { return estado; }
    public void setEstado(EstadoSolicitudEnum estado) { this.estado = estado; }
    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }
    public Convocatoria getConvocatoria() { return convocatoria; }
    public void setConvocatoria(Convocatoria convocatoria) { this.convocatoria = convocatoria; }
    public List<Preferencia> getPreferencias() { return preferencias; }
    public void setPreferencias(List<Preferencia> preferencias) { this.preferencias = preferencias; }
    public Asignacion getAsignacion() { return asignacion; }
    public void setAsignacion(Asignacion asignacion) { this.asignacion = asignacion; }
}