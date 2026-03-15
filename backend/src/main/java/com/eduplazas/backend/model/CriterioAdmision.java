package com.eduplazas.backend.model;

import jakarta.persistence.*;

@Entity
public class CriterioAdmision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String asignatura;
    private double peso;

    @ManyToOne
    @JoinColumn(name = "oferta_id")
    private Oferta oferta;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }
    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }
}