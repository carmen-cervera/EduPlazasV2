package com.eduplazas.backend.service;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private final EstudianteRepository estudianteRepository;
    private final RepresentanteUniversidadRepository representanteRepository;
    private final UniversidadRepository universidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthService(EstudianteRepository estudianteRepository,
                       RepresentanteUniversidadRepository representanteRepository,
                       UniversidadRepository universidadRepository,
                       UsuarioRepository usuarioRepository) {
        this.estudianteRepository = estudianteRepository;
        this.representanteRepository = representanteRepository;
        this.universidadRepository = universidadRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // REGISTRO ESTUDIANTE
    public String registrarEstudiante(String nombre, String apellidos, String email,
                                      String password, String dni, String idEvau) throws Exception {
        if (usuarioRepository.existsByEmail(email)) {
            return "ERROR: El email ya está registrado";
        }
        if (usuarioRepository.existsByDni(dni)) {
            return "ERROR: El DNI ya está registrado";
        }
        if (estudianteRepository.existsByIdEvau(idEvau)) {
            return "ERROR: El ID de EvAU ya está registrado";
        }
        if (!validarEvau(idEvau, nombre, apellidos)) {
            return "ERROR: El ID de EvAU no coincide con el nombre y apellidos proporcionados";
        }

        Estudiante estudiante = new Estudiante();
        estudiante.setNombre(nombre);
        estudiante.setApellidos(apellidos);
        estudiante.setEmail(email);
        estudiante.setPassword(passwordEncoder.encode(password));
        estudiante.setDni(dni);
        estudiante.setIdEvau(idEvau);
        estudiante.setNotaBase(0.0);

        estudianteRepository.save(estudiante);
        return "OK";
    }

    // REGISTRO REPRESENTANTE UNIVERSIDAD
    public String registrarRepresentante(String nombre, String apellidos,
                                         String emailInstitucional, String password,
                                         String dni, Long universidadId) throws Exception {
        if (usuarioRepository.existsByEmail(emailInstitucional)) {
            return "ERROR: El email ya está registrado";
        }
        if (usuarioRepository.existsByDni(dni)) {
            return "ERROR: El DNI ya está registrado";
        }
        if (!validarEmailUniversidad(emailInstitucional)) {
            return "ERROR: El email no tiene una extensión universitaria válida";
        }

        Universidad universidad = universidadRepository.findById(universidadId).orElse(null);
        if (universidad == null) {
            return "ERROR: Universidad no encontrada";
        }

        RepresentanteUniversidad representante = new RepresentanteUniversidad();
        representante.setNombre(nombre);
        representante.setApellidos(apellidos);
        representante.setEmail(emailInstitucional); // email común para login
        representante.setEmailInstitucional(emailInstitucional);
        representante.setPassword(passwordEncoder.encode(password));
        representante.setDni(dni);
        representante.setUniversidad(universidad);

        representanteRepository.save(representante);
        return "OK";
    }

    // LOGIN — busca por email en todos los tipos de usuario
    public Usuario login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) return null;
        if (!passwordEncoder.matches(password, usuario.getPassword())) return null;
        return usuario;
    }

    // Validar ID EvAU contra el archivo JSON
    private boolean validarEvau(String idEvau, String nombre, String apellidos) throws Exception {
        InputStream is = new ClassPathResource("evau.json").getInputStream();
        JsonNode lista = objectMapper.readTree(is);
        for (JsonNode entrada : lista) {
            if (entrada.get("id").asText().equals(idEvau) &&
                entrada.get("nombre").asText().equalsIgnoreCase(nombre) &&
                entrada.get("apellidos").asText().equalsIgnoreCase(apellidos)) {
                return true;
            }
        }
        return false;
    }

    // Validar extensión de email universitario
    private boolean validarEmailUniversidad(String email) throws Exception {
        InputStream is = new ClassPathResource("universidades-email.json").getInputStream();
        JsonNode lista = objectMapper.readTree(is);
        for (JsonNode entrada : lista) {
            if (email.endsWith(entrada.get("extension").asText())) {
                return true;
            }
        }
        return false;
    }

    public List<Universidad> obtenerUniversidades() {
        return universidadRepository.findAll();
    }
}