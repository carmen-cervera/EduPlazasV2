package com.eduplazas.backend.service;

import com.eduplazas.backend.model.Solicitud;
import com.eduplazas.backend.repository.SolicitudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;

    public SolicitudService(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    public List<Solicitud> obtenerTodas() {
        return solicitudRepository.findAll();
    }

    public Solicitud obtenerPorId(Long id) {
        return solicitudRepository.findById(id).orElse(null);
    }

    public Solicitud guardar(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }
}