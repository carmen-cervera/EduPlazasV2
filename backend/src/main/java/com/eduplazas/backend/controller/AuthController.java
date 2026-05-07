package com.eduplazas.backend.controller;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
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

    // Login — devuelve datos del usuario + token JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            Map<String, Object> respuesta = authService.loginConToken(
                body.get("email"),
                body.get("password")
            );
            if (respuesta == null) {
                return ResponseEntity.badRequest().body("Email o contraseña incorrectos");
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