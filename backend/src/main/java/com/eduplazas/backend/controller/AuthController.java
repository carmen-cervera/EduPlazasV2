package com.eduplazas.backend.controller;

import com.eduplazas.backend.model.Estudiante;
import com.eduplazas.backend.model.RepresentanteUniversidad;
import com.eduplazas.backend.model.Universidad;
import com.eduplazas.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Registro estudiante
    @PostMapping("/registro/estudiante")
    public ResponseEntity<String> registrarEstudiante(@RequestBody Map<String, String> body) {
        try {
            String resultado = authService.registrarEstudiante(
                body.get("nombre"),
                body.get("apellidos"),
                body.get("email"),
                body.get("password"),
                body.get("dni"),
                body.get("idEvau")
            );
            if (resultado.startsWith("ERROR")) {
                return ResponseEntity.badRequest().body(resultado);
            }
            return ResponseEntity.ok("Registro completado correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    // Registro representante universidad
    @PostMapping("/registro/universidad")
    public ResponseEntity<String> registrarRepresentante(@RequestBody Map<String, String> body) {
        try {
            String resultado = authService.registrarRepresentante(
                body.get("nombre"),
                body.get("apellidos"),
                body.get("emailInstitucional"),
                body.get("password"),
                body.get("dni"),
                Long.parseLong(body.get("universidadId"))
            );
            if (resultado.startsWith("ERROR")) {
                return ResponseEntity.badRequest().body(resultado);
            }
            return ResponseEntity.ok("Registro completado correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            Object usuario = authService.login(body.get("email"), body.get("password"));
            if (usuario == null) {
                return ResponseEntity.badRequest().body("Email o contraseña incorrectos");
            }

            Map<String, Object> respuesta = new HashMap<>();

            if (usuario instanceof Estudiante e) {
                respuesta.put("id", e.getId());
                respuesta.put("email", e.getEmail());
                respuesta.put("rol", "ESTUDIANTE");
                respuesta.put("nombre", e.getNombre());
            } else if (usuario instanceof RepresentanteUniversidad r) {
                respuesta.put("id", r.getId());
                respuesta.put("email", r.getEmailInstitucional());
                respuesta.put("rol", "UNIVERSIDAD");
                respuesta.put("nombre", r.getNombre());
                if (r.getUniversidad() != null) {
                    respuesta.put("universidad", Map.of(
                        "id", r.getUniversidad().getId(),
                        "nombre", r.getUniversidad().getNombre()
                    ));
                }
            }

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    // Lista de universidades para el formulario de registro
    @GetMapping("/universidades")
    public ResponseEntity<List<Universidad>> obtenerUniversidades() {
        return ResponseEntity.ok(authService.obtenerUniversidades());
    }
}