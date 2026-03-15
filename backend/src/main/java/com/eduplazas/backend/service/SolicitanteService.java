package com.eduplazas.backend.service;

import com.eduplazas.backend.model.Solicitante;
import com.eduplazas.backend.repository.SolicitanteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitanteService {

    private final SolicitanteRepository solicitanteRepository;

    public SolicitanteService(SolicitanteRepository solicitanteRepository) {
        this.solicitanteRepository = solicitanteRepository;
    }

    public List<Solicitante> obtenerTodos() {
        return solicitanteRepository.findAll();
    }

    public Solicitante obtenerPorId(Long id) {
        return solicitanteRepository.findById(id).orElse(null);
    }

    public Solicitante guardar(Solicitante solicitante) {
        return solicitanteRepository.save(solicitante);
    }
}