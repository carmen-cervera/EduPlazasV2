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
    private final EmailService emailService;

    public AsignacionService(SolicitudRepository solicitudRepository,
                             AsignacionRepository asignacionRepository,
                             OfertaRepository ofertaRepository,
                             PreferenciaRepository preferenciaRepository,
                             EmailService emailService) {
        this.solicitudRepository = solicitudRepository;
        this.asignacionRepository = asignacionRepository;
        this.ofertaRepository = ofertaRepository;
        this.preferenciaRepository = preferenciaRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void procesarAsignaciones(Long convocatoriaId) {

        List<Solicitud> solicitudes = solicitudRepository.findByConvocatoriaId(convocatoriaId)
            .stream()
            .filter(s -> s.getEstado() == EstadoSolicitudEnum.ENTREGADA)
            .collect(Collectors.toList());
        List<Oferta> ofertas = ofertaRepository.findByConvocatoriaId(convocatoriaId);

        // Calcular notas ponderadas: estudianteId -> ofertaId -> nota
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

        // Candidatos por oferta ordenados por nota desc
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

        // Algoritmo de asignación
        Map<Long, Long> asignacionFinal = new HashMap<>();
        for (Solicitud solicitud : solicitudes) {
            Estudiante estudiante = solicitud.getEstudiante();
            List<Preferencia> preferencias = solicitud.getPreferencias().stream()
                .sorted(Comparator.comparingInt(Preferencia::getOrdenPreferencia))
                .collect(Collectors.toList());

            for (Preferencia preferencia : preferencias) {
                Long ofertaId = preferencia.getOferta().getId();
                Oferta oferta = ofertas.stream()
                    .filter(o -> o.getId().equals(ofertaId))
                    .findFirst().orElse(null);
                if (oferta == null) continue;

                List<Solicitud> candidatos = candidatosPorOferta.get(ofertaId);
                int posicion = -1;
                for (int i = 0; i < candidatos.size(); i++) {
                    if (candidatos.get(i).getEstudiante().getId().equals(estudiante.getId())) {
                        posicion = i;
                        break;
                    }
                }
                if (posicion >= 0 && posicion < oferta.getTotalPlazas()) {
                    asignacionFinal.put(estudiante.getId(), ofertaId);
                    break;
                }
            }
        }

        // Calcular nota de corte por oferta y guardarla
        for (Oferta oferta : ofertas) {
            List<Solicitud> candidatos = candidatosPorOferta.get(oferta.getId());
            // Estudiantes efectivamente admitidos en esta oferta
            List<Solicitud> admitidos = candidatos.stream()
                .filter(s -> oferta.getId().equals(asignacionFinal.get(s.getEstudiante().getId())))
                .collect(Collectors.toList());

            if (!admitidos.isEmpty()) {
                // El último admitido (menor nota) marca la nota de corte
                double notaCorte = admitidos.stream()
                    .mapToDouble(s -> notasPonderadas.get(s.getEstudiante().getId()).get(oferta.getId()))
                    .min()
                    .orElse(0.0);
                oferta.setNotaCorte(notaCorte);
                ofertaRepository.save(oferta);
            }
        }

        // Guardar asignaciones y enviar emails
        String cursoAcademico = solicitudes.isEmpty() ? "actual" :
            solicitudes.get(0).getConvocatoria().getCursoAcademico();

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

                // Email: asignado
                try {
                    emailService.enviarResultadoAsignado(
                        estudiante.getEmail(),
                        estudiante.getNombre(),
                        cursoAcademico,
                        oferta.getGrado(),
                        oferta.getUniversidad().getNombre(),
                        oferta.getNotaCorte()
                    );
                } catch (Exception e) {
                    System.err.println("Error enviando email a " + estudiante.getEmail() + ": " + e.getMessage());
                }

            } else {
                solicitud.setEstado(EstadoSolicitudEnum.RECHAZADA);

                // Email: rechazado
                try {
                    emailService.enviarResultadoRechazado(
                        estudiante.getEmail(),
                        estudiante.getNombre(),
                        cursoAcademico
                    );
                } catch (Exception e) {
                    System.err.println("Error enviando email a " + estudiante.getEmail() + ": " + e.getMessage());
                }
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

    public List<Map<String, Object>> obtenerTablaOferta(Long ofertaId) {
        Oferta oferta = ofertaRepository.findById(ofertaId).orElse(null);
        if (oferta == null) return List.of();

        Long convocatoriaId = oferta.getConvocatoria().getId();
        List<Oferta> todasOfertas = ofertaRepository.findByConvocatoriaId(convocatoriaId);
        List<Solicitud> solicitudes = solicitudRepository.findByConvocatoriaId(convocatoriaId);

        List<Solicitud> candidatos = solicitudes.stream()
            .filter(s -> s.getPreferencias().stream()
                .anyMatch(p -> p.getOferta().getId().equals(ofertaId)))
            .collect(Collectors.toList());

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

            boolean tienePlazaSuperior = tienePlazaEnPreferenciaSuperior(
                solicitud, ofertaId, todasOfertas, solicitudes);

            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("estudianteId", estudiante.getId());
            fila.put("nombre", estudiante.getNombre() + " " + estudiante.getApellidos());
            fila.put("notaPonderada", nota);
            fila.put("tienePlazaSuperior", tienePlazaSuperior);
            tabla.add(fila);
        }

        tabla.sort((a, b) -> Double.compare(
            (double) b.get("notaPonderada"), (double) a.get("notaPonderada")));

        return tabla;
    }

    private boolean tienePlazaEnPreferenciaSuperior(Solicitud solicitud, Long ofertaId,
                                                     List<Oferta> todasOfertas,
                                                     List<Solicitud> todasSolicitudes) {
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