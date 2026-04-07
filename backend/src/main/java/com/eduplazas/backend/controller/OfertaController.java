package com.eduplazas.backend.controller;

import com.eduplazas.backend.model.CriterioAdmision;
import com.eduplazas.backend.model.Oferta;
import com.eduplazas.backend.service.OfertaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ofertas")
@CrossOrigin(origins = "http://localhost:5173")
public class OfertaController {

    private final OfertaService ofertaService;

    public OfertaController(OfertaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    //Recibe datos del formulario y llama al service para crear la oferta y guardar en la bbdd
    @PostMapping
    public ResponseEntity<?> publicarOferta(@RequestBody Map<String, Object> body) {
        try {
            Long usuarioId = Long.valueOf(body.get("usuarioId").toString());
            String grado = body.get("grado").toString();
            int plazas = Integer.parseInt(body.get("plazas").toString());

            List<Map<String, Object>> criteriosRaw = (List<Map<String, Object>>) body.get("criterios");
            List<CriterioAdmision> criterios = null;
            if (criteriosRaw != null) {
                criterios = criteriosRaw.stream().map(c -> {
                    CriterioAdmision criterio = new CriterioAdmision();
                    criterio.setAsignatura(c.get("asignatura").toString());
                    criterio.setPeso(Double.parseDouble(c.get("peso").toString()));
                    return criterio;
                }).toList();
            }

            Oferta oferta = ofertaService.publicarOferta(usuarioId, grado, plazas, criterios);
            return ResponseEntity.ok(oferta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Redcibe el usuario Id y devuelve solo las ofertas de esa unviersidad.
    @GetMapping("/mi-universidad")
    public ResponseEntity<?> obtenerOfertasMiUniversidad(@RequestParam Long usuarioId) {
        try {
            return ResponseEntity.ok(ofertaService.obtenerPorUsuario(usuarioId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Devuelve todas las ofertas de todas las unviersidades.
    @GetMapping
    public ResponseEntity<List<Oferta>> obtenerTodas() {
        return ResponseEntity.ok(ofertaService.obtenerTodas());
    }
}
