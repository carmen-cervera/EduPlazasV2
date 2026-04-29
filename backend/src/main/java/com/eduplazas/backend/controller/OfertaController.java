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

    // Publicar oferta (representante universidad)
    @PostMapping
    public ResponseEntity<?> publicarOferta(@RequestBody Map<String, Object> body) {
        try {
            Long representanteId = Long.valueOf(body.get("representanteId").toString());
            String grado = body.get("grado").toString();
            int totalPlazas = Integer.parseInt(body.get("totalPlazas").toString());

            List<Map<String, Object>> criteriosRaw =
                    (List<Map<String, Object>>) body.get("criterios");
            List<CriterioAdmision> criterios = null;
            if (criteriosRaw != null) {
                criterios = criteriosRaw.stream().map(c -> {
                    CriterioAdmision criterio = new CriterioAdmision();
                    criterio.setAsignatura(c.get("asignatura").toString());
                    criterio.setPeso(Double.parseDouble(c.get("peso").toString()));
                    return criterio;
                }).toList();
            }

            Oferta oferta = ofertaService.publicarOferta(representanteId, grado,
                    totalPlazas, criterios);
            return ResponseEntity.ok(oferta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Ver ofertas de mi universidad
    @GetMapping("/mi-universidad")
    public ResponseEntity<?> obtenerOfertasMiUniversidad(@RequestParam Long representanteId) {
        try {
            return ResponseEntity.ok(ofertaService.obtenerPorRepresentante(representanteId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Ver todas las ofertas
    @GetMapping
    public ResponseEntity<List<Oferta>> obtenerTodas() {
        return ResponseEntity.ok(ofertaService.obtenerTodas());
    }
}