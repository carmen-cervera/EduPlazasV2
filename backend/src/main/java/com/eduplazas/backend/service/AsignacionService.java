package com.eduplazas.backend.service;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.repository.*;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    @Transactional
    public void procesarAsignaciones(Long universidadId) {

        // 1. Ofertas de esta universidad
        List<Oferta> ofertasUniversidad = ofertaRepository.findAll().stream()
                .filter(o -> o.getUniversidad() != null && o.getUniversidad().getId().equals(universidadId))
                .collect(Collectors.toList());

        Set<Long> idsOfertas = ofertasUniversidad.stream()
                .map(Oferta::getId)
                .collect(Collectors.toSet());

        Map<Long, Oferta> ofertasById = ofertasUniversidad.stream()
                .collect(Collectors.toMap(Oferta::getId, o -> o));

        // 2. Borrar asignaciones previas de esta universidad para recalcular desde cero
        List<Asignacion> existentesPropias = asignacionRepository.findAll().stream()
                .filter(a -> idsOfertas.contains(a.getOferta().getId()))
                .collect(Collectors.toList());
        asignacionRepository.deleteAll(existentesPropias);
        asignacionRepository.flush();

        // 3. Solicitudes que incluyen al menos una oferta de esta universidad
        List<Solicitud> solicitudes = solicitudRepository.findAll().stream()
                .filter(s -> s.getPreferencias().stream().anyMatch(o -> idsOfertas.contains(o.getId())))
                .collect(Collectors.toList());

        // 4. Mapa de preferencias globales del alumno (todas las ofertas, en orden)
        //    para poder comparar si una oferta es mejor o peor que una asignación existente
        Map<Long, List<Long>> preferenciasGlobales = new HashMap<>();
        for (Solicitud s : solicitudes) {
            List<Long> prefs = s.getPreferencias().stream()
                    .map(Oferta::getId)
                    .collect(Collectors.toList());
            preferenciasGlobales.put(s.getSolicitante().getId(), prefs);
        }

        // 5. Filtrar: excluir alumnos que ya tienen una asignación de OTRA universidad
        //    para una oferta con MEJOR prioridad que cualquiera de esta universidad
        Map<Long, Solicitante> solicitantesById = new HashMap<>();
        Map<Long, Map<Long, Double>> notasMap = new HashMap<>();

        for (Solicitud s : solicitudes) {
            Solicitante sol = s.getSolicitante();
            List<Long> prefsGlobales = preferenciasGlobales.get(sol.getId());

            // Mejor índice que esta universidad puede ofrecer al alumno
            int mejorIndiceEstaUniv = prefsGlobales.stream()
                    .filter(idsOfertas::contains)
                    .mapToInt(prefsGlobales::indexOf)
                    .min()
                    .orElse(Integer.MAX_VALUE);

            // Índice de la asignación existente de otra universidad (si la hay)
            List<Asignacion> asignacionesOtras = asignacionRepository.findAllBySolicitanteId(sol.getId()).stream()
                    .filter(a -> !idsOfertas.contains(a.getOferta().getId()))
                    .collect(Collectors.toList());

            boolean yaTieneAsignacionMejor = asignacionesOtras.stream().anyMatch(a -> {
                int indiceExistente = prefsGlobales.indexOf(a.getOferta().getId());
                return indiceExistente >= 0 && indiceExistente < mejorIndiceEstaUniv;
            });

            if (yaTieneAsignacionMejor) continue; // Ya está mejor asignado, no participar

            solicitantesById.put(sol.getId(), sol);

            // Calcular nota ponderada por oferta de esta universidad
            Map<Long, Double> notasPorOferta = new HashMap<>();
            for (Oferta o : ofertasUniversidad) {
                double nota = sol.getNotaBase();
                for (CriterioAdmision c : o.getCriterios()) {
                    for (NotaAsignatura na : sol.getNotas()) {
                        if (na.getAsignatura().equals(c.getAsignatura())) {
                            nota += na.getNota() * c.getPeso();
                            break;
                        }
                    }
                }
                notasPorOferta.put(o.getId(), nota);
            }
            notasMap.put(sol.getId(), notasPorOferta);
        }

        // 6. Preferencias filtradas a solo las ofertas de esta universidad
        Map<Long, List<Long>> preferencias = new HashMap<>();
        for (Long studentId : solicitantesById.keySet()) {
            List<Long> prefsGlobales = preferenciasGlobales.get(studentId);
            List<Long> prefs = prefsGlobales.stream()
                    .filter(idsOfertas::contains)
                    .collect(Collectors.toList());
            preferencias.put(studentId, prefs);
        }

        // 7. Algoritmo de Gale-Shapley
        Map<Long, Map<Long, Double>> aceptados = new HashMap<>();
        for (Oferta o : ofertasUniversidad) {
            aceptados.put(o.getId(), new HashMap<>());
        }

        Map<Long, Integer> nextProposal = new HashMap<>();
        for (Long id : solicitantesById.keySet()) {
            nextProposal.put(id, 0);
        }

        Queue<Long> libres = new LinkedList<>(solicitantesById.keySet());

        while (!libres.isEmpty()) {
            Long studentId = libres.poll();
            List<Long> prefs = preferencias.get(studentId);
            int idx = nextProposal.get(studentId);

            if (idx >= prefs.size()) continue; // sin más opciones en esta universidad

            Long ofertaId = prefs.get(idx);
            nextProposal.put(studentId, idx + 1);

            double nota = notasMap.get(studentId).get(ofertaId);
            Map<Long, Double> aceptadosOferta = aceptados.get(ofertaId);
            int maxPlazas = ofertasById.get(ofertaId).getPlazas();

            if (aceptadosOferta.size() < maxPlazas) {
                aceptadosOferta.put(studentId, nota);
            } else {
                Long peorId = null;
                double peorNota = Double.MAX_VALUE;
                for (Map.Entry<Long, Double> e : aceptadosOferta.entrySet()) {
                    if (e.getValue() < peorNota) {
                        peorNota = e.getValue();
                        peorId = e.getKey();
                    }
                }
                if (nota > peorNota) {
                    aceptadosOferta.remove(peorId);
                    aceptadosOferta.put(studentId, nota);
                    libres.add(peorId);
                } else {
                    libres.add(studentId);
                }
            }
        }

        // 8. Guardar asignaciones finales
        //    Si el alumno tenía asignación de otra universidad para peor prioridad → eliminarla
        for (Map.Entry<Long, Map<Long, Double>> entry : aceptados.entrySet()) {
            Oferta oferta = ofertasById.get(entry.getKey());
            for (Map.Entry<Long, Double> alumno : entry.getValue().entrySet()) {
                Long studentId = alumno.getKey();
                Solicitante solicitante = solicitantesById.get(studentId);
                List<Long> prefsGlobales = preferenciasGlobales.get(studentId);
                int indiceNuevo = prefsGlobales.indexOf(oferta.getId());

                // Eliminar asignaciones de otras universidades con peor prioridad
                List<Asignacion> asignacionesOtras = asignacionRepository.findAllBySolicitanteId(solicitante.getId()).stream()
                        .filter(a -> !idsOfertas.contains(a.getOferta().getId()))
                        .collect(Collectors.toList());

                for (Asignacion existente : asignacionesOtras) {
                    int indiceExistente = prefsGlobales.indexOf(existente.getOferta().getId());
                    if (indiceExistente < 0 || indiceExistente > indiceNuevo) {
                        asignacionRepository.delete(existente);
                    }
                }

                Asignacion asignacion = new Asignacion();
                asignacion.setSolicitante(solicitante);
                asignacion.setOferta(oferta);
                asignacion.setNotaFinal(alumno.getValue());
                asignacion.setEstado("ASIGNADA");
                asignacionRepository.save(asignacion);
            }
        }
    }

    public List<Asignacion> obtenerTodas() {
        return asignacionRepository.findAll();
    }

    public Optional<Asignacion> obtenerPorUsuario(Long usuarioId) {
        // Si hay varias asignaciones (no debería ocurrir), devolver la de mejor prioridad
        List<Asignacion> todas = asignacionRepository.findAllBySolicitanteUsuarioId(usuarioId);
        if (todas.isEmpty()) return Optional.empty();
        if (todas.size() == 1) return Optional.of(todas.get(0));
        // Fallback: devolver la de mayor nota final
        return todas.stream().max(Comparator.comparingDouble(Asignacion::getNotaFinal));
    }

}
