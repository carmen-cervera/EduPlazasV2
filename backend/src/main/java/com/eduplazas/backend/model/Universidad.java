package com.eduplazas.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "universidades")
public class Universidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String extensionEmail;

    @OneToMany(mappedBy = "universidad", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Oferta> ofertas = new ArrayList<>();

    @OneToMany(mappedBy = "universidad")
    @JsonIgnore
    private List<RepresentanteUniversidad> representantes = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getExtensionEmail() { return extensionEmail; }
    public void setExtensionEmail(String extensionEmail) { this.extensionEmail = extensionEmail; }
    public List<Oferta> getOfertas() { return ofertas; }
    public void setOfertas(List<Oferta> ofertas) { this.ofertas = ofertas; }
    public List<RepresentanteUniversidad> getRepresentantes() { return representantes; }
    public void setRepresentantes(List<RepresentanteUniversidad> representantes) { this.representantes = representantes; }
}