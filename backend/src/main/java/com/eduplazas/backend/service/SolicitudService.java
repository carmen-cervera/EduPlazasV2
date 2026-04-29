package com.eduplazas.backend.service;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final EstudianteRepository estudianteRepository;
    private final ConvocatoriaRepository convocatoriaRepository;
    private final OfertaRepository ofertaRepository;
    private final NotaAsignaturaRepository notaAsignaturaRepository;
    private final PreferenciaRepository preferenciaRepository;

    public SolicitudService(SolicitudRepository solicitudRepository,
                            EstudianteRepository estudianteRepository,
                            ConvocatoriaRepository convocatoriaRepository,
                            OfertaRepository ofertaRepository,
                            NotaAsignaturaRepository notaAsignaturaRepository,
                            PreferenciaRepository preferenciaRepository) {
        this.solicitudRepository = solicitudRepository;
        this.estudianteRepository = estudianteRepository;
        this.convocatoriaRepository = convocatoriaRepository;
        this.ofertaRepository = ofertaRepository;
        this.notaAsignaturaRepository = notaAsignaturaRepository;
        this.preferenciaRepository = preferenciaRepository;
    }

    @Transactional
    public Solicitud crearSolicitud(Long estudianteId, Long convocatoriaId,
                                    List<Long> ofertaIdsOrdenadas) {

        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("ERROR: Estudiante no encontrado"));

        Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaId)
                .orElseThrow(() -> new RuntimeException("ERROR: Convocatoria no encontrada"));

        if (convocatoria.getEstado() != EstadoConvocatoriaEnum.ABIERTA) {
            throw new RuntimeException("La convocatoria no está abierta");
        }

        if (solicitudRepository.findByEstudianteIdAndConvocatoriaId(
                estudianteId, convocatoriaId).isPresent()) {
            throw new RuntimeException("Ya tienes una solicitud para esta convocatoria");
        }

        if (ofertaIdsOrdenadas == null || ofertaIdsOrdenadas.isEmpty()) {
            throw new RuntimeException("ERROR: Debes seleccionar al menos una oferta");
        }

        Set<Long> sinDuplicados = new HashSet<>(ofertaIdsOrdenadas);
        if (sinDuplicados.size() != ofertaIdsOrdenadas.size()) {
            throw new RuntimeException("No puede haber grados repetidos en la solicitud");
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setEstudiante(estudiante);
        solicitud.setConvocatoria(convocatoria);
        solicitud.setEstado(EstadoSolicitudEnum.ENTREGADA);
        solicitud.setFechaPresentacion(LocalDate.now());
        solicitudRepository.save(solicitud);

        for (int i = 0; i < ofertaIdsOrdenadas.size(); i++) {
            Oferta oferta = ofertaRepository.findById(ofertaIdsOrdenadas.get(i))
                    .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

            if (!oferta.getConvocatoria().getId().equals(convocatoriaId)) {
                throw new RuntimeException("Todas las ofertas deben pertenecer a la convocatoria");
            }

            Preferencia preferencia = new Preferencia();
            preferencia.setSolicitud(solicitud);
            preferencia.setOferta(oferta);
            preferencia.setOrdenPreferencia(i + 1);
            preferenciaRepository.save(preferencia);
        }

        return solicitud;
    }

    public Optional<Solicitud> obtenerPorEstudiante(Long estudianteId) {
        return solicitudRepository.findByEstudianteId(estudianteId);
    }

    public Optional<Convocatoria> obtenerConvocatoriaAbierta() {
        return convocatoriaRepository.findByEstado(EstadoConvocatoriaEnum.ABIERTA);
    }

    public List<Oferta> obtenerOfertasPorConvocatoria(Long convocatoriaId) {
        return ofertaRepository.findByConvocatoriaId(convocatoriaId);
    }

    private static final Set<String> ASIGNATURAS_COMUNES = Set.of(
        "Lengua Castellana", "Historia de España", "Inglés", "Matemáticas"
    );

    @Transactional
    public void guardarNotas(Long estudianteId, List<NotaAsignatura> notas) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        notaAsignaturaRepository.deleteByEstudianteId(estudianteId);

        double notaBase = 0.0;
        for (NotaAsignatura nota : notas) {
            if ("Bachillerato".equals(nota.getAsignatura())) {
                notaBase += nota.getNota() * 0.6;
            } else if (ASIGNATURAS_COMUNES.contains(nota.getAsignatura())) {
                notaBase += nota.getNota() * 0.1;
            }
            nota.setEstudiante(estudiante);
            notaAsignaturaRepository.save(nota);
        }

        estudiante.setNotaBase(notaBase);
        estudianteRepository.save(estudiante);
    }
}