package com.eduplazas.backend.model;

import jakarta.persistence.*;

@Entity
public class Universidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String provincia;
    private boolean listaParaAsignar = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    public boolean isListaParaAsignar() { return listaParaAsignar; }
    public void setListaParaAsignar(boolean listaParaAsignar) { this.listaParaAsignar = listaParaAsignar; }
}