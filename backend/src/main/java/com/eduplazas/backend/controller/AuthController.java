package com.eduplazas.backend.controller;

import com.eduplazas.backend.model.Universidad;
import com.eduplazas.backend.model.Usuario;
import com.eduplazas.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // Registro universidad
    @PostMapping("/registro/universidad")
    public ResponseEntity<String> registrarUniversidad(@RequestBody Map<String, String> body) {
        try {
            String resultado = authService.registrarUniversidad(
                body.get("nombreContacto"),
                body.get("apellidosContacto"),
                body.get("email"),
                body.get("password"),
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
            Usuario usuario = authService.login(body.get("email"), body.get("password"));
            if (usuario == null) {
                return ResponseEntity.badRequest().body("Email o contraseña incorrectos");
            }
            return ResponseEntity.ok(Map.of(
                "id", usuario.getId(),
                "email", usuario.getEmail(),
                "rol", usuario.getRol()
            ));
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