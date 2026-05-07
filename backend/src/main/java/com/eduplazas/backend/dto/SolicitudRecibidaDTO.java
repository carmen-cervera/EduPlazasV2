package com.eduplazas.backend.dto;

public class SolicitudRecibidaDTO {
    private Long idSolicitud;
    private String nombreEstudiante;
    private String nombreGrado;
    private Integer ordenPreferencia;
    private String estado;

    public SolicitudRecibidaDTO() {
    }

    public SolicitudRecibidaDTO(Long idSolicitud, String nombreEstudiante, String nombreGrado, Integer ordenPreferencia,
            String estado) {
        this.idSolicitud = idSolicitud;
        this.nombreEstudiante = nombreEstudiante;
        this.nombreGrado = nombreGrado;
        this.ordenPreferencia = ordenPreferencia;
        this.estado = estado;
    }

    public Long getIdSolicitud() {
        return idSolicitud;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public String getNombreGrado() {
        return nombreGrado;
    }

    public Integer getOrdenPreferencia() {
        return ordenPreferencia;
    }

    public String getEstado() {
        return estado;
    }

}
