package com.eduplazas.backend.service;

import com.eduplazas.backend.model.Convocatoria;
import com.eduplazas.backend.repository.ConvocatoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConvocatoriaService {

    private final ConvocatoriaRepository convocatoriaRepository;

    public ConvocatoriaService(ConvocatoriaRepository convocatoriaRepository) {
        this.convocatoriaRepository = convocatoriaRepository;
    }

    public List<Convocatoria> obtenerTodas() {
        return convocatoriaRepository.findAll();
    }

    public Convocatoria obtenerPorId(Long id) {
        return convocatoriaRepository.findById(id).orElse(null);
    }

    public Convocatoria guardar(Convocatoria convocatoria) {
        return convocatoriaRepository.save(convocatoria);
    }
}