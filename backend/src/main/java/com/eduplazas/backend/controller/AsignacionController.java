package com.eduplazas.backend.controller;

import com.eduplazas.backend.model.Asignacion;
import com.eduplazas.backend.service.AsignacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/asignaciones")
@CrossOrigin(origins = "http://localhost:5173")
public class AsignacionController {

    private final AsignacionService asignacionService;

    public AsignacionController(AsignacionService asignacionService) {
        this.asignacionService = asignacionService;
    }

    // Ver todos los resultados de asignación
    @GetMapping
    public ResponseEntity<List<Asignacion>> obtenerTodas() {
        return ResponseEntity.ok(asignacionService.obtenerTodas());
    }

    // Ver la asignación de un estudiante concreto
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<?> obtenerMiAsignacion(@PathVariable Long estudianteId) {
        return asignacionService.obtenerPorEstudiante(estudianteId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Ver tabla de candidatos de una oferta (con columna tienePlazaSuperior)
    @GetMapping("/tabla-oferta/{ofertaId}")
    public ResponseEntity<List<Map<String, Object>>> obtenerTablaOferta(
            @PathVariable Long ofertaId) {
        return ResponseEntity.ok(asignacionService.obtenerTablaOferta(ofertaId));
    }
}