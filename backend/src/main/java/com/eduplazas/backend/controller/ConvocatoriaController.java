package com.eduplazas.backend.controller;

import com.eduplazas.backend.model.Convocatoria;
import com.eduplazas.backend.service.ConvocatoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/convocatorias")
public class ConvocatoriaController {

    private final ConvocatoriaService convocatoriaService;

    public ConvocatoriaController(ConvocatoriaService convocatoriaService) {
        this.convocatoriaService = convocatoriaService;
    }

    @GetMapping
    public ResponseEntity<List<Convocatoria>> obtenerTodas() {
        return ResponseEntity.ok(convocatoriaService.obtenerTodas());
    }

    @PostMapping
    public ResponseEntity<?> crearConvocatoria(@RequestBody Map<String, String> body) {
        try {
            String cursoAcademico = body.get("cursoAcademico");
            LocalDate fechaApertura = LocalDate.parse(body.get("fechaApertura"));
            LocalDate fechaCierre = LocalDate.parse(body.get("fechaCierre"));

            Convocatoria nueva = convocatoriaService.crearConvocatoria(
                cursoAcademico, fechaApertura, fechaCierre
            );
            return ResponseEntity.ok(nueva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}