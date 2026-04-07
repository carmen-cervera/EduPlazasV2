package com.eduplazas.backend.service;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConvocatoriaRepository convocatoriaRepository;
    private final CriterioAdmisionRepository criterioAdmisionRepository;

    public OfertaService(OfertaRepository ofertaRepository, UsuarioRepository usuarioRepository,
                         ConvocatoriaRepository convocatoriaRepository,
                         CriterioAdmisionRepository criterioAdmisionRepository) {
        this.ofertaRepository = ofertaRepository;
        this.usuarioRepository = usuarioRepository;
        this.convocatoriaRepository = convocatoriaRepository;
        this.criterioAdmisionRepository = criterioAdmisionRepository;
    }

    public List<Oferta> obtenerTodas() {
        return ofertaRepository.findAll();
    }

    public Oferta obtenerPorId(Long id) {
        return ofertaRepository.findById(id).orElse(null);
    }

    public Oferta guardar(Oferta oferta) {
        return ofertaRepository.save(oferta);
    }
    //Será lo que se ejecuta cuando una universidad rellena el formulario de publicar oferta.
public Oferta publicarOferta(Long usuarioId, String grado, int plazas, List<CriterioAdmision> criterios) {
    Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    if (!"UNIVERSIDAD".equals(usuario.getRol())) {
        throw new RuntimeException("Solo las universidades pueden publicar ofertas");
    }

    if (usuario.getUniversidad() == null) {
        throw new RuntimeException("El usuario no tiene universidad asociada");
    }

    Convocatoria convocatoria = convocatoriaRepository.findByEstado("ABIERTA")
            .orElseThrow(() -> new RuntimeException("No hay ninguna convocatoria abierta"));

    Oferta oferta = new Oferta();
    oferta.setGrado(grado);
    oferta.setPlazas(plazas);
    oferta.setUniversidad(usuario.getUniversidad());
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

//Para que la universidad solo vea los grados que ella tiene publicados
public List<Oferta> obtenerPorUsuario(Long usuarioId) {
    Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    if (usuario.getUniversidad() == null) return List.of();
    Long universidadId = usuario.getUniversidad().getId();
    return ofertaRepository.findAll().stream()
            .filter(o -> o.getUniversidad() != null && o.getUniversidad().getId().equals(universidadId))
            .toList();
}
}

