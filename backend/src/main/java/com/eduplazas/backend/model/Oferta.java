package com.eduplazas.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String grado;
    private int plazas;

    @ManyToOne
    @JoinColumn(name = "universidad_id")
    private Universidad universidad;

    @ManyToOne
    @JoinColumn(name = "convocatoria_id")
    private Convocatoria convocatoria;

    @OneToMany(mappedBy = "oferta", cascade = CascadeType.ALL)
    private List<CriterioAdmision> criterios;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }
    public int getPlazas() { return plazas; }
    public void setPlazas(int plazas) { this.plazas = plazas; }
    public Universidad getUniversidad() { return universidad; }
    public void setUniversidad(Universidad universidad) { this.universidad = universidad; }
    public Convocatoria getConvocatoria() { return convocatoria; }
    public void setConvocatoria(Convocatoria convocatoria) { this.convocatoria = convocatoria; }
    public List<CriterioAdmision> getCriterios() { return criterios; }
    public void setCriterios(List<CriterioAdmision> criterios) { this.criterios = criterios; }
}