package com.eduplazas.backend.service;

import com.eduplazas.backend.model.Oferta;
import com.eduplazas.backend.repository.OfertaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfertaService {

    private final OfertaRepository ofertaRepository;

    public OfertaService(OfertaRepository ofertaRepository) {
        this.ofertaRepository = ofertaRepository;
    }

    public List<Oferta> obtenerTodas() {
        return ofertaRepository.findAll();
    }

    public Oferta obtenerPorId(Long id) {
        return ofertaRepository.findById(id).orElse(null);
    }

    public Oferta guardar(Oferta oferta) {
        return ofertaRepository.save(oferta);
    }
}