package com.eduplazas.backend.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Usuario {
    // Sin campos adicionales, hereda todo de Usuario
}