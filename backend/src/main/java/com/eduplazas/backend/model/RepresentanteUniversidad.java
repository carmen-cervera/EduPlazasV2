package com.eduplazas.backend.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("REPRESENTANTE")
public class RepresentanteUniversidad extends Usuario {

    private String emailInstitucional;

    @ManyToOne
    @JoinColumn(name = "universidad_id")
    private Universidad universidad;

    public String getEmailInstitucional() { return emailInstitucional; }
    public void setEmailInstitucional(String emailInstitucional) { this.emailInstitucional = emailInstitucional; }
    public Universidad getUniversidad() { return universidad; }
    public void setUniversidad(Universidad universidad) { this.universidad = universidad; }
}