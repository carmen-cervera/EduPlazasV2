package com.eduplazas.backend.service;

import com.eduplazas.backend.model.Universidad;
import com.eduplazas.backend.model.Usuario;
import com.eduplazas.backend.repository.UniversidadRepository;
import com.eduplazas.backend.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UniversidadRepository universidadRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthService(UsuarioRepository usuarioRepository,
                       UniversidadRepository universidadRepository) {
        this.usuarioRepository = usuarioRepository;
        this.universidadRepository = universidadRepository;
    }

    // REGISTRO ESTUDIANTE
    public String registrarEstudiante(String nombre, String apellidos, String email,
                                       String password, String dni, String idEvau) throws Exception {

        // Email ya registrado
        if (usuarioRepository.existsByEmail(email)) {
            return "ERROR: El email ya está registrado";
        }

        // DNI ya registrado
        if (usuarioRepository.existsByDni(dni)) {
            return "ERROR: El DNI ya está registrado";
        }

        // ID EvAU ya registrado
        if (usuarioRepository.existsByIdEvau(idEvau)) {
            return "ERROR: El ID de EvAU ya está registrado";
        }

        // Validar que el ID EvAU coincide con nombre y apellidos en el archivo JSON
        if (!validarEvau(idEvau, nombre, apellidos)) {
            return "ERROR: El ID de EvAU no coincide con el nombre y apellidos proporcionados";
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setDni(dni);
        usuario.setIdEvau(idEvau);
        usuario.setRol("ESTUDIANTE");

        usuarioRepository.save(usuario);
        return "OK";
    }

    // REGISTRO UNIVERSIDAD
    public String registrarUniversidad(String nombreContacto, String apellidosContacto,
                                        String email, String password,
                                        Long universidadId) throws Exception {

        // Email ya registrado
        if (usuarioRepository.existsByEmail(email)) {
            return "ERROR: El email ya está registrado";
        }

        // Validar extensión del email
        if (!validarEmailUniversidad(email)) {
            return "ERROR: El email no tiene una extensión universitaria válida";
        }

        // Comprobar que la universidad existe
        Universidad universidad = universidadRepository.findById(universidadId).orElse(null);
        if (universidad == null) {
            return "ERROR: Universidad no encontrada";
        }

        Usuario usuario = new Usuario();
        usuario.setNombreContacto(nombreContacto);
        usuario.setApellidosContacto(apellidosContacto);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol("UNIVERSIDAD");
        usuario.setUniversidad(universidad);

        usuarioRepository.save(usuario);
        return "OK";
    }

    // LOGIN
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