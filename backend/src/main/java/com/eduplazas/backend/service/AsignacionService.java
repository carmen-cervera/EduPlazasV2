package com.eduplazas.backend.service;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AsignacionService {

    private final SolicitudRepository solicitudRepository;
    private final AsignacionRepository asignacionRepository;
    private final OfertaRepository ofertaRepository;

    public AsignacionService(SolicitudRepository solicitudRepository,
                             AsignacionRepository asignacionRepository,
                             OfertaRepository ofertaRepository) {
        this.solicitudRepository = solicitudRepository;
        this.asignacionRepository = asignacionRepository;
        this.ofertaRepository = ofertaRepository;
    }

    public void procesarAsignaciones(Long universidadId) {

        // Solo las ofertas de esta universidad
        List<Oferta> ofertasUniversidad = ofertaRepository.findAll().stream()
                .filter(o -> o.getUniversidad() != null && o.getUniversidad().getId().equals(universidadId))
                .collect(java.util.stream.Collectors.toList());

        Set<Long> idsOfertasUniversidad = ofertasUniversidad.stream()
                .map(Oferta::getId)
                .collect(java.util.stream.Collectors.toSet());

        Map<Long, Integer> plazasDisponibles = new HashMap<>();
        for (Oferta oferta : ofertasUniversidad) {
            plazasDisponibles.put(oferta.getId(), oferta.getPlazas());
        }

        // Solo solicitudes que incluyan al menos una oferta de esta universidad
        List<Solicitud> solicitudes = solicitudRepository.findAll().stream()
                .filter(s -> s.getPreferencias().stream()
                        .anyMatch(o -> idsOfertasUniversidad.contains(o.getId())))
                .collect(java.util.stream.Collectors.toList());

        solicitudes.sort((a, b) ->
            Double.compare(b.getSolicitante().getNotaBase(),
                           a.getSolicitante().getNotaBase()));

        // Solicitantes ya asignados en alguna oferta de ESTA universidad
        Set<Long> yaAsignadoEnEstaUniversidad = new HashSet<>();

        for (Solicitud solicitud : solicitudes) {
            Solicitante solicitante = solicitud.getSolicitante();

            if (yaAsignadoEnEstaUniversidad.contains(solicitante.getId())) continue;

            for (Oferta oferta : solicitud.getPreferencias()) {
                // Ignorar ofertas de otras universidades
                if (!idsOfertasUniversidad.contains(oferta.getId())) continue;

                double notaPonderada = solicitante.getNotaBase();
                for (CriterioAdmision criterio : oferta.getCriterios()) {
                    for (NotaAsignatura nota : solicitante.getNotas()) {
                        if (nota.getAsignatura().equals(criterio.getAsignatura())) {
                            notaPonderada += nota.getNota() * criterio.getPeso();
                        }
                    }
                }

                int plazas = plazasDisponibles.getOrDefault(oferta.getId(), 0);
                if (plazas > 0) {
                    Asignacion asignacion = new Asignacion();
                    asignacion.setSolicitante(solicitante);
                    asignacion.setOferta(oferta);
                    asignacion.setNotaFinal(notaPonderada);
                    asignacion.setEstado("ASIGNADA");
                    asignacionRepository.save(asignacion);

                    plazasDisponibles.put(oferta.getId(), plazas - 1);
                    yaAsignadoEnEstaUniversidad.add(solicitante.getId());
                    break;
                }
            }
        }
    }

    public List<Asignacion> obtenerTodas() {
        return asignacionRepository.findAll();
    }
    
    public Optional<Asignacion> obtenerPorUsuario(Long usuarioId) {
    return asignacionRepository.findBySolicitanteUsuarioId(usuarioId);
    }

}