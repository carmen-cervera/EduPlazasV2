package com.eduplazas.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("ESTUDIANTE")
public class Estudiante extends Usuario {

    private String idEvau;
    private double notaBase;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<NotaAsignatura> notas;

    @OneToOne(mappedBy = "estudiante", cascade = CascadeType.ALL)
    @JsonIgnore
    private Solicitud solicitud;

    public String getIdEvau() { return idEvau; }
    public void setIdEvau(String idEvau) { this.idEvau = idEvau; }
    public double getNotaBase() { return notaBase; }
    public void setNotaBase(double notaBase) { this.notaBase = notaBase; }
    public List<NotaAsignatura> getNotas() { return notas; }
    public void setNotas(List<NotaAsignatura> notas) { this.notas = notas; }
    public Solicitud getSolicitud() { return solicitud; }
    public void setSolicitud(Solicitud solicitud) { this.solicitud = solicitud; }
}