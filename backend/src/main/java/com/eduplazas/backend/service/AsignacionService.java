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

    public void procesarAsignaciones() {

        Map<Long, Integer> plazasDisponibles = new HashMap<>();
        for (Oferta oferta : ofertaRepository.findAll()) {
            plazasDisponibles.put(oferta.getId(), oferta.getPlazas());
        }

        Map<Long, Boolean> yaAsignado = new HashMap<>();

        List<Solicitud> solicitudes = solicitudRepository.findAll();
        solicitudes.sort((a, b) ->
            Double.compare(b.getSolicitante().getNotaBase(),
                           a.getSolicitante().getNotaBase()));

        for (Solicitud solicitud : solicitudes) {
            Solicitante solicitante = solicitud.getSolicitante();

            if (yaAsignado.getOrDefault(solicitante.getId(), false)) continue;

            for (Oferta oferta : solicitud.getPreferencias()) {

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
                    yaAsignado.put(solicitante.getId(), true);
                    break;
                }
            }
        }
    }

    public List<Asignacion> obtenerTodas() {
        return asignacionRepository.findAll();
    }
}