package com.eduplazas.backend.model;

import jakarta.persistence.*;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String rol; // ESTUDIANTE, UNIVERSIDAD

    // Campos solo para ESTUDIANTE
    private String nombre;
    private String apellidos;
    private String dni;
    private String idEvau;

    //Añadido por Luis (se puede modificar)
    private Double nota;

    // Campos solo para UNIVERSIDAD
    private String nombreContacto;
    private String apellidosContacto;

    @ManyToOne
    @JoinColumn(name = "universidad_id")
    private Universidad universidad;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getIdEvau() { return idEvau; }
    public void setIdEvau(String idEvau) { this.idEvau = idEvau; }
    public String getNombreContacto() { return nombreContacto; }
    public void setNombreContacto(String nombreContacto) { this.nombreContacto = nombreContacto; }
    public String getApellidosContacto() { return apellidosContacto; }
    public void setApellidosContacto(String apellidosContacto) { this.apellidosContacto = apellidosContacto; }
    public Universidad getUniversidad() { return universidad; }
    public void setUniversidad(Universidad universidad) { this.universidad = universidad; }
    public Double getNota() { return nota; }
    public void setNota(Double nota) { this.nota = nota; }
}