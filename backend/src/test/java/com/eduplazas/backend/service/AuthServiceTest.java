package com.eduplazas.backend.service;

import com.eduplazas.backend.config.JwtUtil;
import com.eduplazas.backend.model.Estudiante;
import com.eduplazas.backend.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Caso 1 — AuthService.registrarEstudiante con datos válidos.
 *
 * La validación de EvAU lee src/test/resources/evau.json (sobrescribe la de producción
 * en el classpath de test), que contiene la entrada MAD-2025-401 / Isabel / Gutiérrez Cruz.
 *
 * BCryptPasswordEncoder es un campo final de AuthService inicializado directamente,
 * por lo que no se inyecta como mock: se usa el encoder real y se verifica el formato
 * del hash resultante.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock EstudianteRepository estudianteRepository;
    @Mock RepresentanteUniversidadRepository representanteRepository;
    @Mock UniversidadRepository universidadRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock JwtUtil jwtUtil;

    @InjectMocks AuthService authService;

    @Test
    void registrarEstudiante_conDatosValidos_retornaOkYGuardaConPasswordHasheada() throws Exception {
        // given
        String nombre    = "Isabel";
        String apellidos = "Gutiérrez Cruz";
        String email     = "nuevo.estudiante@email.com";
        String password  = "Contrasena1!";
        String dni       = "99999999X";
        String idEvau    = "MAD-2025-401";

        when(usuarioRepository.existsByEmail(email)).thenReturn(false);
        when(usuarioRepository.existsByDni(dni)).thenReturn(false);
        when(estudianteRepository.existsByIdEvau(idEvau)).thenReturn(false);
        when(estudianteRepository.save(any(Estudiante.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        String resultado = authService.registrarEstudiante(nombre, apellidos, email, password, dni, idEvau);

        // then — retorna OK
        assertThat(resultado).isEqualTo("OK");

        // then — save invocado exactamente una vez
        ArgumentCaptor<Estudiante> captor = ArgumentCaptor.forClass(Estudiante.class);
        verify(estudianteRepository, times(1)).save(captor.capture());

        // then — la contraseña almacenada está hasheada (formato BCrypt $2a$...)
        Estudiante guardado = captor.getValue();
        assertThat(guardado.getPassword())
                .isNotEqualTo(password)
                .startsWith("$2a$");
    }
}
