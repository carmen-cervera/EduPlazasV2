package com.eduplazas.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class NotaAsignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String asignatura;
    private double nota;

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    @JsonIgnore
    private Solicitante solicitante;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }
    public double getNota() { return nota; }
    public void setNota(double nota) { this.nota = nota; }
    public Solicitante getSolicitante() { return solicitante; }
    public void setSolicitante(Solicitante solicitante) { this.solicitante = solicitante; }
}