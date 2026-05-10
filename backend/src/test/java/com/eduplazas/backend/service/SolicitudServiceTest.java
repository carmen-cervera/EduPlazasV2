package com.eduplazas.backend.service;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Caso 5 — SolicitudService.crearSolicitud con envío de notificación.
 *
 * Datos del DataLoader (seed=42):
 *   Estudiante id=7, email=estudiante1@eduplazas.es, nombre=Ana, apellidos=García Fernández
 *   Convocatoria id=1, cursoAcademico=2025-2026, estado=ABIERTA
 *   Ofertas preferidas: [8, 6, 9, 7, 15]
 *
 * Nota sobre solicitudRepository.save():
 *   El método crearOActualizarSolicitud llama a save() DOS veces para una solicitud nueva:
 *   una primera vez al crear la entidad (para obtener ID antes de vincular preferencias)
 *   y una segunda al actualizar el estado. La verificación refleja este comportamiento real.
 */
@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock SolicitudRepository        solicitudRepository;
    @Mock EstudianteRepository       estudianteRepository;
    @Mock ConvocatoriaRepository     convocatoriaRepository;
    @Mock OfertaRepository           ofertaRepository;
    @Mock NotaAsignaturaRepository   notaAsignaturaRepository;
    @Mock PreferenciaRepository      preferenciaRepository;
    @Mock EmailService               emailService;

    @InjectMocks SolicitudService solicitudService;

    @Test
    void crearSolicitud_sinSolicitudPrevia_quedaEntregadaConPreferenciasOrdenadas() {
        // ── Entidades ────────────────────────────────────────────────────────
        Estudiante est = new Estudiante();
        est.setId(7L);
        est.setNombre("Ana");
        est.setApellidos("García Fernández");
        est.setEmail("estudiante1@eduplazas.es");

        Convocatoria conv = new Convocatoria();
        conv.setId(1L);
        conv.setCursoAcademico("2025-2026");
        conv.setEstado(EstadoConvocatoriaEnum.ABIERTA);

        Universidad uni = new Universidad();
        uni.setNombre("Universidad Politécnica de Madrid");

        Oferta o8  = oferta(8L,  "Ingeniería Aeroespacial",        conv, uni);
        Oferta o6  = oferta(6L,  "Ingeniería Informática",          conv, uni);
        Oferta o9  = oferta(9L,  "Ingeniería de Telecomunicación",  conv, uni);
        Oferta o7  = oferta(7L,  "Ingeniería Industrial",           conv, uni);
        Oferta o15 = oferta(15L, "Ingeniería Informática UC3M",     conv, uni);

        // ── Mocks ────────────────────────────────────────────────────────────
        when(estudianteRepository.findById(7L)).thenReturn(Optional.of(est));
        when(convocatoriaRepository.findById(1L)).thenReturn(Optional.of(conv));
        when(solicitudRepository.findByEstudianteIdAndConvocatoriaId(7L, 1L))
                .thenReturn(Optional.empty());

        // Primer save asigna ID; segundo save actualiza estado
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> {
            Solicitud s = invocation.getArgument(0);
            if (s.getId() == null) s.setId(1L);
            return s;
        });

        when(ofertaRepository.findById(8L)).thenReturn(Optional.of(o8));
        when(ofertaRepository.findById(6L)).thenReturn(Optional.of(o6));
        when(ofertaRepository.findById(9L)).thenReturn(Optional.of(o9));
        when(ofertaRepository.findById(7L)).thenReturn(Optional.of(o7));
        when(ofertaRepository.findById(15L)).thenReturn(Optional.of(o15));

        // ── Ejecución ────────────────────────────────────────────────────────
        Solicitud result = solicitudService.crearSolicitud(7L, 1L, List.of(8L, 6L, 9L, 7L, 15L));

        // ── Verificaciones ───────────────────────────────────────────────────

        // Estado ENTREGADA
        assertThat(result.getEstado()).isEqualTo(EstadoSolicitudEnum.ENTREGADA);

        // fechaPresentacion = hoy
        assertThat(result.getFechaPresentacion()).isEqualTo(LocalDate.now());

        // save llamado 2 veces: creación + actualización de estado
        verify(solicitudRepository, times(2)).save(any(Solicitud.class));

        // Preferencias guardadas exactamente 5 veces, en el orden indicado
        ArgumentCaptor<Preferencia> prefCaptor = ArgumentCaptor.forClass(Preferencia.class);
        verify(preferenciaRepository, times(5)).save(prefCaptor.capture());

        List<Preferencia> prefs = prefCaptor.getAllValues();
        assertThat(prefs.get(0).getOrdenPreferencia()).isEqualTo(1);
        assertThat(prefs.get(0).getOferta().getId()).isEqualTo(8L);
        assertThat(prefs.get(1).getOrdenPreferencia()).isEqualTo(2);
        assertThat(prefs.get(1).getOferta().getId()).isEqualTo(6L);
        assertThat(prefs.get(2).getOrdenPreferencia()).isEqualTo(3);
        assertThat(prefs.get(2).getOferta().getId()).isEqualTo(9L);
        assertThat(prefs.get(3).getOrdenPreferencia()).isEqualTo(4);
        assertThat(prefs.get(3).getOferta().getId()).isEqualTo(7L);
        assertThat(prefs.get(4).getOrdenPreferencia()).isEqualTo(5);
        assertThat(prefs.get(4).getOferta().getId()).isEqualTo(15L);

        // Email de confirmación enviado una vez al email correcto
        verify(emailService, times(1)).enviarConfirmacionSolicitud(
                eq("estudiante1@eduplazas.es"),
                eq("Ana"),
                eq("2025-2026"),
                anyList()
        );
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private Oferta oferta(Long id, String grado, Convocatoria conv, Universidad uni) {
        Oferta o = new Oferta();
        o.setId(id);
        o.setGrado(grado);
        o.setConvocatoria(conv);
        o.setUniversidad(uni);
        return o;
    }
}
