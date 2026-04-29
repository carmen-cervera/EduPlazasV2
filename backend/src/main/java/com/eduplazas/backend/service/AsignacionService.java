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
    private final PreferenciaRepository preferenciaRepository;

    public AsignacionService(SolicitudRepository solicitudRepository,
                             AsignacionRepository asignacionRepository,
                             OfertaRepository ofertaRepository,
                             PreferenciaRepository preferenciaRepository) {
        this.solicitudRepository = solicitudRepository;
        this.asignacionRepository = asignacionRepository;
        this.ofertaRepository = ofertaRepository;
        this.preferenciaRepository = preferenciaRepository;
    }

    @Transactional
    public void procesarAsignaciones(Long convocatoriaId) {

        // 1. Obtener todas las solicitudes de la convocatoria
        List<Solicitud> solicitudes = solicitudRepository.findByConvocatoriaId(convocatoriaId);

        // 2. Obtener todas las ofertas de la convocatoria
        List<Oferta> ofertas = ofertaRepository.findByConvocatoriaId(convocatoriaId);

        // 3. Para cada solicitud, calcular la nota ponderada por oferta
        // notasPonderadas: estudianteId -> ofertaId -> nota
        Map<Long, Map<Long, Double>> notasPonderadas = new HashMap<>();

        for (Solicitud solicitud : solicitudes) {
            Estudiante estudiante = solicitud.getEstudiante();
            Map<Long, Double> notasPorOferta = new HashMap<>();

            for (Oferta oferta : ofertas) {
                double nota = estudiante.getNotaBase();
                for (CriterioAdmision criterio : oferta.getCriterios()) {
                    for (NotaAsignatura na : estudiante.getNotas()) {
                        if (na.getAsignatura().equals(criterio.getAsignatura())) {
                            nota += na.getNota() * criterio.getPeso();
                            break;
                        }
                    }
                }
                notasPorOferta.put(oferta.getId(), nota);
            }
            notasPonderadas.put(estudiante.getId(), notasPorOferta);
        }

        // 4. Para cada oferta, construir la lista ordenada de candidatos por nota ponderada
        // ofertaId -> lista de solicitudes ordenadas por nota desc
        Map<Long, List<Solicitud>> candidatosPorOferta = new HashMap<>();
        for (Oferta oferta : ofertas) {
            List<Solicitud> candidatos = solicitudes.stream()
                .filter(s -> s.getPreferencias().stream()
                    .anyMatch(p -> p.getOferta().getId().equals(oferta.getId())))
                .sorted(Comparator.comparingDouble(s ->
                    -notasPonderadas.get(s.getEstudiante().getId()).get(oferta.getId())))
                .collect(Collectors.toList());
            candidatosPorOferta.put(oferta.getId(), candidatos);
        }

        // 5. Algoritmo de asignación:
        // Para cada estudiante, obtener su preferencia de mayor orden donde quede dentro de las plazas
        // estudianteId -> ofertaId asignada (null si no se asigna)
        Map<Long, Long> asignacionFinal = new HashMap<>();

        for (Solicitud solicitud : solicitudes) {
            Estudiante estudiante = solicitud.getEstudiante();

            // Preferencias ordenadas de mayor a menor prioridad
            List<Preferencia> preferencias = solicitud.getPreferencias().stream()
                .sorted(Comparator.comparingInt(Preferencia::getOrdenPreferencia))
                .collect(Collectors.toList());

            for (Preferencia preferencia : preferencias) {
                Long ofertaId = preferencia.getOferta().getId();
                Oferta oferta = ofertas.stream()
                    .filter(o -> o.getId().equals(ofertaId))
                    .findFirst().orElse(null);
                if (oferta == null) continue;

                // Posición del estudiante en la lista de esta oferta
                List<Solicitud> candidatos = candidatosPorOferta.get(ofertaId);
                int posicion = -1;
                for (int i = 0; i < candidatos.size(); i++) {
                    if (candidatos.get(i).getEstudiante().getId().equals(estudiante.getId())) {
                        posicion = i;
                        break;
                    }
                }

                // Entra en plazas y no tiene asignación en preferencia superior
                if (posicion >= 0 && posicion < oferta.getTotalPlazas()) {
                    asignacionFinal.put(estudiante.getId(), ofertaId);
                    break; // se asigna a la primera oferta donde entra
                }
            }
        }

        // 6. Guardar asignaciones y actualizar estado de solicitudes
        for (Solicitud solicitud : solicitudes) {
            Estudiante estudiante = solicitud.getEstudiante();
            Long ofertaIdAsignada = asignacionFinal.get(estudiante.getId());

            if (ofertaIdAsignada != null) {
                Oferta oferta = ofertas.stream()
                    .filter(o -> o.getId().equals(ofertaIdAsignada))
                    .findFirst().orElse(null);

                Asignacion asignacion = new Asignacion();
                asignacion.setSolicitud(solicitud);
                asignacion.setOferta(oferta);
                asignacion.setNotaFinal(notasPonderadas.get(estudiante.getId()).get(ofertaIdAsignada));
                asignacion.setEstado(EstadoAsignacionEnum.ASIGNADA);
                asignacionRepository.save(asignacion);

                solicitud.setEstado(EstadoSolicitudEnum.ASIGNADA);
            } else {
                solicitud.setEstado(EstadoSolicitudEnum.RECHAZADA);
            }
            solicitudRepository.save(solicitud);
        }
    }

    public List<Asignacion> obtenerTodas() {
        return asignacionRepository.findAll();
    }

    public Optional<Asignacion> obtenerPorEstudiante(Long estudianteId) {
        return solicitudRepository.findByEstudianteId(estudianteId)
            .flatMap(s -> asignacionRepository.findBySolicitudId(s.getId()));
    }

    // Tabla de candidatos por oferta con columna "tienePlazaSuperior"
    public List<Map<String, Object>> obtenerTablaOferta(Long ofertaId) {
        Oferta oferta = ofertaRepository.findById(ofertaId).orElse(null);
        if (oferta == null) return List.of();

        Long convocatoriaId = oferta.getConvocatoria().getId();
        List<Oferta> todasOfertas = ofertaRepository.findByConvocatoriaId(convocatoriaId);
        List<Solicitud> solicitudes = solicitudRepository.findByConvocatoriaId(convocatoriaId);

        // Filtrar solicitudes que incluyen esta oferta
        List<Solicitud> candidatos = solicitudes.stream()
            .filter(s -> s.getPreferencias().stream()
                .anyMatch(p -> p.getOferta().getId().equals(ofertaId)))
            .collect(Collectors.toList());

        // Calcular nota ponderada para esta oferta
        List<Map<String, Object>> tabla = new ArrayList<>();
        for (Solicitud solicitud : candidatos) {
            Estudiante estudiante = solicitud.getEstudiante();
            double nota = estudiante.getNotaBase();
            for (CriterioAdmision criterio : oferta.getCriterios()) {
                for (NotaAsignatura na : estudiante.getNotas()) {
                    if (na.getAsignatura().equals(criterio.getAsignatura())) {
                        nota += na.getNota() * criterio.getPeso();
                        break;
                    }
                }
            }

            // Comprobar si tiene plaza en preferencia superior
            boolean tienePlazaSuperior = tienePlazaEnPreferenciaSuperior(
                solicitud, ofertaId, todasOfertas, solicitudes);

            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("estudianteId", estudiante.getId());
            fila.put("nombre", estudiante.getNombre() + " " + estudiante.getApellidos());
            fila.put("notaPonderada", nota);
            fila.put("tienePlazaSuperior", tienePlazaSuperior);
            tabla.add(fila);
        }

        // Ordenar por nota ponderada descendente
        tabla.sort((a, b) -> Double.compare(
            (double) b.get("notaPonderada"), (double) a.get("notaPonderada")));

        return tabla;
    }

    private boolean tienePlazaEnPreferenciaSuperior(Solicitud solicitud, Long ofertaId,
                                                     List<Oferta> todasOfertas,
                                                     List<Solicitud> todasSolicitudes) {
        // Preferencias con orden menor (=mayor prioridad) que la oferta actual
        int ordenActual = solicitud.getPreferencias().stream()
            .filter(p -> p.getOferta().getId().equals(ofertaId))
            .mapToInt(Preferencia::getOrdenPreferencia)
            .findFirst().orElse(Integer.MAX_VALUE);

        List<Long> ofertasSuperiores = solicitud.getPreferencias().stream()
            .filter(p -> p.getOrdenPreferencia() < ordenActual)
            .map(p -> p.getOferta().getId())
            .collect(Collectors.toList());

        for (Long ofertaSuperiorId : ofertasSuperiores) {
            Oferta ofertaSuperior = todasOfertas.stream()
                .filter(o -> o.getId().equals(ofertaSuperiorId))
                .findFirst().orElse(null);
            if (ofertaSuperior == null) continue;

            // Ver posición en esa oferta superior
            List<Solicitud> candidatosSuperior = todasSolicitudes.stream()
                .filter(s -> s.getPreferencias().stream()
                    .anyMatch(p -> p.getOferta().getId().equals(ofertaSuperiorId)))
                .sorted(Comparator.comparingDouble(s -> {
                    double n = s.getEstudiante().getNotaBase();
                    for (CriterioAdmision c : ofertaSuperior.getCriterios()) {
                        for (NotaAsignatura na : s.getEstudiante().getNotas()) {
                            if (na.getAsignatura().equals(c.getAsignatura())) {
                                n += na.getNota() * c.getPeso();
                                break;
                            }
                        }
                    }
                    return -n;
                }))
                .collect(Collectors.toList());

            int posicion = -1;
            for (int i = 0; i < candidatosSuperior.size(); i++) {
                if (candidatosSuperior.get(i).getEstudiante().getId()
                        .equals(solicitud.getEstudiante().getId())) {
                    posicion = i;
                    break;
                }
            }
            if (posicion >= 0 && posicion < ofertaSuperior.getTotalPlazas()) {
                return true;
            }
        }
        return false;
    }
}