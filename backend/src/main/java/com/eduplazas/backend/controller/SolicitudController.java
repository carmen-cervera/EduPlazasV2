package com.eduplazas.backend.controller;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.service.SolicitudService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/solicitudes")
@CrossOrigin(origins = "http://localhost:5173")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // Ver la convocatoria abierta
    @GetMapping("/convocatoria-abierta")
    public ResponseEntity<?> obtenerConvocatoriaAbierta() {
        return solicitudService.obtenerConvocatoriaAbierta()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body("No hay convocatoria abierta"));
    }

    // Ver ofertas de una convocatoria
    @GetMapping("/ofertas")
    public ResponseEntity<List<Oferta>> obtenerOfertas(@RequestParam Long convocatoriaId) {
        return ResponseEntity.ok(solicitudService.obtenerOfertasPorConvocatoria(convocatoriaId));
    }

    // Ver mi solicitud
    @GetMapping("/ver-solicitud/{estudianteId}")
    public ResponseEntity<?> obtenerSolicitud(@PathVariable Long estudianteId) {
        return solicitudService.obtenerPorEstudiante(estudianteId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().body("No existe ninguna solicitud para este estudiante"));
    }

    // Crear solicitud
    @PostMapping
    public ResponseEntity<?> crearSolicitud(@RequestBody Map<String, Object> body) {
        try {
            Long estudianteId = Long.valueOf(body.get("estudianteId").toString());
            Long convocatoriaId = Long.valueOf(body.get("convocatoriaId").toString());
            List<Long> ofertaIds = ((List<?>) body.get("ofertaIds")).stream()
                    .map(o -> Long.valueOf(o.toString()))
                    .toList();

            Solicitud solicitud = solicitudService.crearSolicitud(estudianteId, convocatoriaId, ofertaIds);
            return ResponseEntity.ok(solicitud);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Guardar notas EvAU del estudiante
    @PutMapping("/estudiante/{estudianteId}/notas")
    public ResponseEntity<?> guardarNotas(@PathVariable Long estudianteId,
                                          @RequestBody List<NotaAsignatura> notas) {
        try {
            solicitudService.guardarNotas(estudianteId, notas);
            return ResponseEntity.ok("Notas guardadas correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}