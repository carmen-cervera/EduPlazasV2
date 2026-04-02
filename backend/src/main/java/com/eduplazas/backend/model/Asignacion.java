package com.eduplazas.backend.model;

import jakarta.persistence.*;

@Entity
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    private Solicitante solicitante;

    @ManyToOne
    @JoinColumn(name = "oferta_id")
    private Oferta oferta;

    private double notaFinal;
    private String estado;

    public Asignacion(Solicitante solicitante, Oferta oferta, double notaFinal, String estado) {
        this.solicitante = solicitante;
        this.oferta = oferta;
        this.notaFinal = notaFinal;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Solicitante getSolicitante() { return solicitante; }
    public void setSolicitante(Solicitante solicitante) { this.solicitante = solicitante; }
    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }
    public double getNotaFinal() { return notaFinal; }
    public void setNotaFinal(double notaFinal) { this.notaFinal = notaFinal; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}