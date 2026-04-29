package com.eduplazas.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "preferencias")
public class Preferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int ordenPreferencia;

    @ManyToOne
    @JoinColumn(name = "solicitud_id")
    @JsonIgnore
    private Solicitud solicitud;

    @ManyToOne
    @JoinColumn(name = "oferta_id")
    private Oferta oferta;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getOrdenPreferencia() { return ordenPreferencia; }
    public void setOrdenPreferencia(int ordenPreferencia) { this.ordenPreferencia = ordenPreferencia; }
    public Solicitud getSolicitud() { return solicitud; }
    public void setSolicitud(Solicitud solicitud) { this.solicitud = solicitud; }
    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }
}