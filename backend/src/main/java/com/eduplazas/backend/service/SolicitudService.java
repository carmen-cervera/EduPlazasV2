package com.eduplazas.backend.service;

import com.eduplazas.backend.model.Convocatoria;
import com.eduplazas.backend.model.NotaAsignatura;
import com.eduplazas.backend.model.Oferta;
import com.eduplazas.backend.model.Solicitante;
import com.eduplazas.backend.model.Solicitud;
import com.eduplazas.backend.repository.ConvocatoriaRepository;
import com.eduplazas.backend.repository.NotaAsignaturaRepository;
import com.eduplazas.backend.repository.OfertaRepository;
import com.eduplazas.backend.repository.SolicitanteRepository;
import com.eduplazas.backend.repository.SolicitudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;



@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final SolicitanteRepository solicitanteRepository;
    private final ConvocatoriaRepository convocatoriaRepository;
    private final OfertaRepository ofertaRepository;
    private final NotaAsignaturaRepository notaAsignaturaRepository;

    public SolicitudService(SolicitudRepository solicitudRepository,
                            SolicitanteRepository solicitanteRepository,
                            ConvocatoriaRepository convocatoriaRepository,
                            OfertaRepository ofertaRepository,
                            NotaAsignaturaRepository notaAsignaturaRepository) {
        this.solicitudRepository = solicitudRepository;
        this.solicitanteRepository = solicitanteRepository;
        this.convocatoriaRepository = convocatoriaRepository;
        this.ofertaRepository = ofertaRepository;
        this.notaAsignaturaRepository = notaAsignaturaRepository;
    }
    //CREACIÓN DE LA SOLICITUD
    public Solicitud crearSolicitud(Solicitud solicitudRecibida) {
        //
        if(solicitudRecibida.getSolicitante() == null || solicitudRecibida.getSolicitante().getId() == null){
            throw new RuntimeException("ERROR: Debes indicar que eres el solicitante");
        }
        //
        if (solicitudRecibida.getConvocatoria() == null || solicitudRecibida.getConvocatoria().getId() == null) {
            throw new RuntimeException("ERROR: Debes indicar la convocatoria");
        }
        //
        if (solicitudRecibida.getPreferencias() == null || solicitudRecibida.getPreferencias().isEmpty()) {
            throw new RuntimeException("ERROR: Debes seleccionar al menos una oferta");
        }

        Solicitante solicitante = solicitanteRepository.findById(solicitudRecibida.getSolicitante().getId())
                .orElseThrow(() -> new RuntimeException("ERROR: Solicitante no encontrado"));

        Convocatoria convocatoria = convocatoriaRepository.findById(solicitudRecibida.getConvocatoria().getId())
                .orElseThrow(() -> new RuntimeException("ERROR: Convocatoria no encontrada"));

        if (!"ABIERTA".equalsIgnoreCase(convocatoria.getEstado())) {
            throw new RuntimeException("La convocatoria no está abierta");
        }

        boolean existeSolicitud = solicitudRepository
                .findBySolicitanteIdAndConvocatoriaId(solicitante.getId(), convocatoria.getId())
                .isPresent();

        if (existeSolicitud) {
            throw new RuntimeException("No puedes enviar más de una solicitud para una convocatoria");
        }

        List<Long> idsOfertas = solicitudRecibida.getPreferencias()
                .stream()
                .map(Oferta::getId)
                .collect(Collectors.toList());

        Set<Long> idsSinDuplicados = new HashSet<>(idsOfertas);
        if (idsOfertas.size() != idsSinDuplicados.size()) {
            throw new RuntimeException("No puede haber grados repetidos en la solicitud");
        }

        List<Oferta> ofertasRaw = ofertaRepository.findAllById(idsOfertas);
        Map<Long, Oferta> ofertasMap = ofertasRaw.stream()
                .collect(Collectors.toMap(Oferta::getId, o -> o));
        List<Oferta> ofertas = idsOfertas.stream()
                .map(ofertasMap::get)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        for (Oferta oferta : ofertas) {
            if (!oferta.getConvocatoria().getId().equals(convocatoria.getId())) {
                throw new RuntimeException("Todas las ofertas deben pertenecer a la convocatoria abierta");
            }
        }

        Solicitud nuevaSolicitud = new Solicitud();
        nuevaSolicitud.setSolicitante(solicitante);
        nuevaSolicitud.setConvocatoria(convocatoria);
        nuevaSolicitud.setEstado("ENVIADA");
        nuevaSolicitud.setPreferencias(ofertas);

        return solicitudRepository.save(nuevaSolicitud);


    }
    public Solicitante obtenerSolicitantePorUsuario(Long usuarioId) {
        return solicitanteRepository.findByUsuarioId(usuarioId).orElse(null);
    }

    public Solicitud obtenerSolicitudPorUsuario(Long usuarioId) {
        return solicitudRepository.findBySolicitanteUsuarioId(usuarioId).orElse(null);
    }

    public Convocatoria obtenerConvocatoriaAbierta() {
        return convocatoriaRepository.findByEstado("ABIERTA").orElse(null);
    }

    public List<Oferta> obtenerOfertasPorConvocatoria(Long convocatoriaId) {
        return ofertaRepository.findByConvocatoriaId(convocatoriaId);
    }

    private static final java.util.Set<String> ASIGNATURAS_COMUNES = java.util.Set.of(
        "Lengua Castellana", "Historia de España", "Inglés", "Matemáticas"
    );

    @Transactional
    public void guardarNotas(Long solicitanteId, List<NotaAsignatura> notas) {
        Solicitante solicitante = solicitanteRepository.findById(solicitanteId)
                .orElseThrow(() -> new RuntimeException("Solicitante no encontrado"));

        notaAsignaturaRepository.deleteBySolicitanteId(solicitanteId);

        double notaBase = 0.0;
        for (NotaAsignatura nota : notas) {
            if ("Bachillerato".equals(nota.getAsignatura())) {
                notaBase += nota.getNota() * 0.6;
            } else if (ASIGNATURAS_COMUNES.contains(nota.getAsignatura())) {
                notaBase += nota.getNota() * 0.1;
            }
            nota.setSolicitante(solicitante);
            notaAsignaturaRepository.save(nota);
        }

        solicitante.setNotaBase(notaBase);
        solicitanteRepository.save(solicitante);
    }
}