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
    private final UniversidadRepository universidadRepository;

    public AsignacionService(SolicitudRepository solicitudRepository,
                             AsignacionRepository asignacionRepository,
                             OfertaRepository ofertaRepository,
                             UniversidadRepository universidadRepository) {
        this.solicitudRepository = solicitudRepository;
        this.asignacionRepository = asignacionRepository;
        this.ofertaRepository = ofertaRepository;
        this.universidadRepository = universidadRepository;
    }

    @Transactional
    public void procesarAsignaciones(Long universidadId) {

        // 1. Marcar esta universidad como lista
        Universidad universidad = universidadRepository.findById(universidadId)
                .orElseThrow(() -> new RuntimeException("Universidad no encontrada"));
        universidad.setListaParaAsignar(true);
        universidadRepository.save(universidad);

        // 2. Obtener todas las ofertas de universidades marcadas como listas
        List<Universidad> universidadesListas = universidadRepository.findAll().stream()
                .filter(Universidad::isListaParaAsignar)
                .collect(Collectors.toList());

        Set<Long> idsUniversidadesListas = universidadesListas.stream()
                .map(Universidad::getId)
                .collect(Collectors.toSet());

        List<Oferta> todasLasOfertas = ofertaRepository.findAll().stream()
                .filter(o -> o.getUniversidad() != null && idsUniversidadesListas.contains(o.getUniversidad().getId()))
                .collect(Collectors.toList());

        Set<Long> idsOfertas = todasLasOfertas.stream()
                .map(Oferta::getId)
                .collect(Collectors.toSet());

        Map<Long, Oferta> ofertasById = todasLasOfertas.stream()
                .collect(Collectors.toMap(Oferta::getId, o -> o));

        // 3. Borrar todas las asignaciones del pool actual para recalcular desde cero
        List<Asignacion> existentes = asignacionRepository.findAll().stream()
                .filter(a -> idsOfertas.contains(a.getOferta().getId()))
                .collect(Collectors.toList());
        asignacionRepository.deleteAll(existentes);
        asignacionRepository.flush();

        // 4. Solicitudes que incluyen al menos una oferta del pool
        List<Solicitud> solicitudes = solicitudRepository.findAll().stream()
                .filter(s -> s.getPreferencias().stream().anyMatch(o -> idsOfertas.contains(o.getId())))
                .collect(Collectors.toList());

        // 5. Construir estructuras por solicitante
        Map<Long, Solicitante> solicitantesById = new HashMap<>();
        Map<Long, List<Long>> preferencias = new HashMap<>();  // solo ofertas del pool, en orden
        Map<Long, Map<Long, Double>> notasMap = new HashMap<>();

        for (Solicitud s : solicitudes) {
            Solicitante sol = s.getSolicitante();
            solicitantesById.put(sol.getId(), sol);

            List<Long> prefs = s.getPreferencias().stream()
                    .filter(o -> idsOfertas.contains(o.getId()))
                    .map(Oferta::getId)
                    .collect(Collectors.toList());
            preferencias.put(sol.getId(), prefs);

            Map<Long, Double> notasPorOferta = new HashMap<>();
            for (Oferta o : todasLasOfertas) {
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

        // 6. Algoritmo de Gale-Shapley global
        Map<Long, Map<Long, Double>> aceptados = new HashMap<>();
        for (Oferta o : todasLasOfertas) {
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

            if (idx >= prefs.size()) continue; // sin más opciones en el pool

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

        // 7. Guardar asignaciones finales
        for (Map.Entry<Long, Map<Long, Double>> entry : aceptados.entrySet()) {
            Oferta oferta = ofertasById.get(entry.getKey());
            for (Map.Entry<Long, Double> alumno : entry.getValue().entrySet()) {
                Asignacion asignacion = new Asignacion();
                asignacion.setSolicitante(solicitantesById.get(alumno.getKey()));
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
        List<Asignacion> todas = asignacionRepository.findAllBySolicitanteUsuarioId(usuarioId);
        if (todas.isEmpty()) return Optional.empty();
        if (todas.size() == 1) return Optional.of(todas.get(0));
        return todas.stream().max(Comparator.comparingDouble(Asignacion::getNotaFinal));
    }
}
