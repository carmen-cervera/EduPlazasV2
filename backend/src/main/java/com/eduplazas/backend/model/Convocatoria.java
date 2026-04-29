package com.eduplazas.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "convocatorias")
public class Convocatoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cursoAcademico;
    private LocalDate fechaApertura;
    private LocalDate fechaCierreConvocatoria;

    @Enumerated(EnumType.STRING)
    private EstadoConvocatoriaEnum estado;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCursoAcademico() { return cursoAcademico; }
    public void setCursoAcademico(String cursoAcademico) { this.cursoAcademico = cursoAcademico; }
    public LocalDate getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDate fechaApertura) { this.fechaApertura = fechaApertura; }
    public LocalDate getFechaCierreConvocatoria() { return fechaCierreConvocatoria; }
    public void setFechaCierreConvocatoria(LocalDate fechaCierreConvocatoria) { this.fechaCierreConvocatoria = fechaCierreConvocatoria; }
    public EstadoConvocatoriaEnum getEstado() { return estado; }
    public void setEstado(EstadoConvocatoriaEnum estado) { this.estado = estado; }
}