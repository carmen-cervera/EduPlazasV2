package com.eduplazas.backend.service;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final RepresentanteUniversidadRepository representanteRepository;
    private final ConvocatoriaRepository convocatoriaRepository;
    private final CriterioAdmisionRepository criterioAdmisionRepository;
    private final EmailService emailService;
    private final PreferenciaRepository preferenciaRepository;
    private final AsignacionService asignacionService;

    public OfertaService(OfertaRepository ofertaRepository,
                         RepresentanteUniversidadRepository representanteRepository,
                         ConvocatoriaRepository convocatoriaRepository,
                         CriterioAdmisionRepository criterioAdmisionRepository,
                         EmailService emailService,
                         PreferenciaRepository preferenciaRepository,
                         AsignacionService asignacionService) {
        this.ofertaRepository = ofertaRepository;
        this.representanteRepository = representanteRepository;
        this.convocatoriaRepository = convocatoriaRepository;
        this.criterioAdmisionRepository = criterioAdmisionRepository;
        this.emailService = emailService;
        this.preferenciaRepository = preferenciaRepository;
        this.asignacionService = asignacionService;
    }

    public List<Oferta> obtenerTodas() {
        return ofertaRepository.findAll();
    }

    public Oferta obtenerPorId(Long id) {
        return ofertaRepository.findById(id).orElse(null);
    }

    public Oferta publicarOferta(Long representanteId, String grado, String rama,
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
        oferta.setRama(rama);
        oferta.setTotalPlazas(totalPlazas);
        oferta.setUniversidad(representante.getUniversidad());
        oferta.setConvocatoria(convocatoria);

        Oferta ofertaGuardada = ofertaRepository.save(oferta);

        List<String> nombresAsignaturas = List.of();
        if (criterios != null) {
            for (CriterioAdmision criterio : criterios) {
                criterio.setOferta(ofertaGuardada);
                criterioAdmisionRepository.save(criterio);
            }
            nombresAsignaturas = criterios.stream()
                .map(CriterioAdmision::getAsignatura)
                .toList();
        }

        try {
            emailService.enviarConfirmacionOferta(
                representante.getEmail(),
                representante.getNombre(),
                grado,
                totalPlazas,
                nombresAsignaturas
            );
        } catch (Exception e) {
            System.err.println("Error enviando email de confirmación de oferta: " + e.getMessage());
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

    public List<Map<String, Object>> obtenerPanelUniversidad(Long representanteId) {
        List<Oferta> ofertas = obtenerPorRepresentante(representanteId);
        List<Map<String, Object>> panel = new ArrayList<>();
    
        for (Oferta oferta : ofertas) {
            List<Map<String, Object>> tabla = asignacionService.obtenerTablaOferta(oferta.getId());
    
            List<Map<String, Object>> admitidos = tabla.stream()
            .limit(oferta.getTotalPlazas())
            .filter(f -> !(boolean) f.get("tienePlazaSuperior"))
            .collect(java.util.stream.Collectors.toList());
        
        long numAdmitidos = admitidos.size();
        
        double notaCorte = 0.0;
        if (!admitidos.isEmpty()) {
            notaCorte = (double) admitidos.get(admitidos.size() - 1).get("notaPonderada");
            notaCorte = Math.round(notaCorte * 100.0) / 100.0;
        }
        
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", oferta.getId());
        item.put("grado", oferta.getGrado());
        item.put("rama", oferta.getRama());
        item.put("totalPlazas", oferta.getTotalPlazas());
        item.put("numSolicitudes", numAdmitidos);
        item.put("notaCorteProvisional", notaCorte);
        }
        return panel;
    }
}