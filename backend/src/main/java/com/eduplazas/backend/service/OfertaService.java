package com.eduplazas.backend.service;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final RepresentanteUniversidadRepository representanteRepository;
    private final ConvocatoriaRepository convocatoriaRepository;
    private final CriterioAdmisionRepository criterioAdmisionRepository;

    public OfertaService(OfertaRepository ofertaRepository,
                         RepresentanteUniversidadRepository representanteRepository,
                         ConvocatoriaRepository convocatoriaRepository,
                         CriterioAdmisionRepository criterioAdmisionRepository) {
        this.ofertaRepository = ofertaRepository;
        this.representanteRepository = representanteRepository;
        this.convocatoriaRepository = convocatoriaRepository;
        this.criterioAdmisionRepository = criterioAdmisionRepository;
    }

    public List<Oferta> obtenerTodas() {
        return ofertaRepository.findAll();
    }

    public Oferta obtenerPorId(Long id) {
        return ofertaRepository.findById(id).orElse(null);
    }

    public Oferta publicarOferta(Long representanteId, String grado,
                                  int totalPlazas, List<CriterioAdmision> criterios) {

        RepresentanteUniversidad representante = representanteRepository.findById(representanteId)
                .orElseThrow(() -> new RuntimeException("Representante no encontrado"));

        if (representante.getUniversidad() == null) {
            throw new RuntimeException("El representante no tiene universidad asociada");
        }

        Convocatoria convocatoria = convocatoriaRepository
                .findByEstado(EstadoConvocatoriaEnum.ABIERTA)
                .orElseThrow(() -> new RuntimeException("No hay ninguna convocatoria abierta"));

        Oferta oferta = new Oferta();
        oferta.setGrado(grado);
        oferta.setTotalPlazas(totalPlazas);
        oferta.setUniversidad(representante.getUniversidad());
        oferta.setConvocatoria(convocatoria);

        Oferta ofertaGuardada = ofertaRepository.save(oferta);

        if (criterios != null) {
            for (CriterioAdmision criterio : criterios) {
                criterio.setOferta(ofertaGuardada);
                criterioAdmisionRepository.save(criterio);
            }
        }

        return ofertaGuardada;
    }

    public List<Oferta> obtenerPorRepresentante(Long representanteId) {
        RepresentanteUniversidad representante = representanteRepository.findById(representanteId)
                .orElseThrow(() -> new RuntimeException("Representante no encontrado"));
        if (representante.getUniversidad() == null) return List.of();
        Long universidadId = representante.getUniversidad().getId();
        return ofertaRepository.findAll().stream()
                .filter(o -> o.getUniversidad() != null &&
                             o.getUniversidad().getId().equals(universidadId))
                .toList();
    }
}