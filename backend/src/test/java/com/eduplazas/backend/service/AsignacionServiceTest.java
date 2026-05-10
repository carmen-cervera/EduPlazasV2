package com.eduplazas.backend.service;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Casos 2, 3 y 4 — AsignacionService.procesarAsignaciones.
 *
 * Datos del DataLoader (seed=42) usados en los tres casos:
 *   Estudiante A — id=7, email=estudiante1@eduplazas.es, notaBase=8.42
 *                  Matemáticas II=6.21, Física=5.65
 *                  → notaPonderada (oferta 8) = 8.42 + 6.21×0.2 + 5.65×0.2 = 10.792
 *   Estudiante B — id=8, email=estudiante2@eduplazas.es, notaBase=7.78
 *                  Matemáticas II=5.03, Física=7.52
 *                  → notaPonderada (oferta 8) = 7.78 + 5.03×0.2 + 7.52×0.2 = 10.290
 *
 * Ofertas del DataLoader:
 *   id=8  Ingeniería Aeroespacial (UPM) — criterios: MatemáticasII×0.2, Física×0.2, plazas=80 (aquí 1 para forzar rechazo)
 *   id=6  Ingeniería Informática (UPM)  — criterios: MatemáticasII×0.2, Física×0.1, plazas=120
 */
@ExtendWith(MockitoExtension.class)
class AsignacionServiceTest {

    @Mock SolicitudRepository solicitudRepository;
    @Mock AsignacionRepository asignacionRepository;
    @Mock OfertaRepository ofertaRepository;
    @Mock PreferenciaRepository preferenciaRepository;
    @Mock EmailService emailService;

    @InjectMocks AsignacionService asignacionService;

    // ── Constructores de entidades de prueba ──────────────────────────────────

    private Convocatoria convocatoria() {
        Convocatoria c = new Convocatoria();
        c.setId(1L);
        c.setCursoAcademico("2025-2026");
        return c;
    }

    private Universidad upm() {
        Universidad u = new Universidad();
        u.setNombre("Universidad Politécnica de Madrid");
        return u;
    }

    /** Oferta id=8: Ingeniería Aeroespacial — criterios MathII×0.2, Física×0.2 */
    private Oferta oferta8(Universidad uni, int plazas) {
        CriterioAdmision cMath = new CriterioAdmision();
        cMath.setAsignatura("Matemáticas II");
        cMath.setPeso(0.2);

        CriterioAdmision cFis = new CriterioAdmision();
        cFis.setAsignatura("Física");
        cFis.setPeso(0.2);

        Oferta o = new Oferta();
        o.setId(8L);
        o.setGrado("Ingeniería Aeroespacial");
        o.setTotalPlazas(plazas);
        o.setUniversidad(uni);
        o.setCriterios(List.of(cMath, cFis));
        return o;
    }

    /** Oferta id=6: Ingeniería Informática — criterios MathII×0.2, Física×0.1, plazas=120 */
    private Oferta oferta6(Universidad uni) {
        CriterioAdmision cMath = new CriterioAdmision();
        cMath.setAsignatura("Matemáticas II");
        cMath.setPeso(0.2);

        CriterioAdmision cFis = new CriterioAdmision();
        cFis.setAsignatura("Física");
        cFis.setPeso(0.1);

        Oferta o = new Oferta();
        o.setId(6L);
        o.setGrado("Ingeniería Informática");
        o.setTotalPlazas(120);
        o.setUniversidad(uni);
        o.setCriterios(List.of(cMath, cFis));
        return o;
    }

    /** Estudiante id=7: notaBase=8.42, MathII=6.21, Física=5.65 */
    private Estudiante estudianteA() {
        NotaAsignatura math = new NotaAsignatura();
        math.setAsignatura("Matemáticas II");
        math.setNota(6.21);

        NotaAsignatura fis = new NotaAsignatura();
        fis.setAsignatura("Física");
        fis.setNota(5.65);

        Estudiante e = new Estudiante();
        e.setId(7L);
        e.setNombre("Ana");
        e.setApellidos("García Fernández");
        e.setEmail("estudiante1@eduplazas.es");
        e.setNotaBase(8.42);
        e.setNotas(List.of(math, fis));
        return e;
    }

    /** Estudiante id=8: notaBase=7.78, MathII=5.03, Física=7.52 */
    private Estudiante estudianteB() {
        NotaAsignatura math = new NotaAsignatura();
        math.setAsignatura("Matemáticas II");
        math.setNota(5.03);

        NotaAsignatura fis = new NotaAsignatura();
        fis.setAsignatura("Física");
        fis.setNota(7.52);

        Estudiante e = new Estudiante();
        e.setId(8L);
        e.setNombre("Carlos");
        e.setApellidos("Sánchez Moreno");
        e.setEmail("estudiante2@eduplazas.es");
        e.setNotaBase(7.78);
        e.setNotas(List.of(math, fis));
        return e;
    }

    /** Crea una Solicitud ENTREGADA con las preferencias indicadas en orden. */
    private Solicitud solicitudEntregada(Estudiante est, Convocatoria conv, Oferta... ofertas) {
        Solicitud s = new Solicitud();
        s.setEstudiante(est);
        s.setConvocatoria(conv);
        s.setEstado(EstadoSolicitudEnum.ENTREGADA);

        List<Preferencia> prefs = new ArrayList<>();
        for (int i = 0; i < ofertas.length; i++) {
            Preferencia p = new Preferencia();
            p.setOferta(ofertas[i]);
            p.setOrdenPreferencia(i + 1);
            p.setSolicitud(s);
            prefs.add(p);
        }
        s.setPreferencias(prefs);
        return s;
    }

    // ── Caso 2 ───────────────────────────────────────────────────────────────

    /**
     * Con más candidatos que plazas (1 plaza, 2 candidatos), el candidato con mayor
     * nota ponderada queda ASIGNADO y el otro RECHAZADO.
     * notaPonderada(A)=10.792 > notaPonderada(B)=10.290 → A asignado, B rechazado.
     */
    @Test
    void procesarAsignaciones_masCanditatosQuePlazas_asignaAlDeMayorNota() {
        Convocatoria conv  = convocatoria();
        Universidad  uni   = upm();
        Oferta       o8    = oferta8(uni, 1);   // una sola plaza

        Estudiante estA = estudianteA();
        Estudiante estB = estudianteB();

        Solicitud solA = solicitudEntregada(estA, conv, o8);
        Solicitud solB = solicitudEntregada(estB, conv, o8);

        when(solicitudRepository.findByConvocatoriaId(1L)).thenReturn(List.of(solA, solB));
        when(ofertaRepository.findByConvocatoriaId(1L)).thenReturn(List.of(o8));

        // when
        asignacionService.procesarAsignaciones(1L);

        // then — exactamente una Asignacion creada, para el estudiante A
        ArgumentCaptor<Asignacion> asignCaptor = ArgumentCaptor.forClass(Asignacion.class);
        verify(asignacionRepository, times(1)).save(asignCaptor.capture());

        Asignacion asig = asignCaptor.getValue();
        assertThat(asig.getEstado()).isEqualTo(EstadoAsignacionEnum.ASIGNADA);
        assertThat(asig.getSolicitud().getEstudiante().getId()).isEqualTo(7L);

        // then — solicitud de B guardada con estado RECHAZADA (sin Asignacion)
        ArgumentCaptor<Solicitud> solCaptor = ArgumentCaptor.forClass(Solicitud.class);
        verify(solicitudRepository, times(2)).save(solCaptor.capture());
        assertThat(solCaptor.getAllValues())
                .anyMatch(s -> s.getEstado() == EstadoSolicitudEnum.RECHAZADA
                            && s.getEstudiante().getId().equals(8L));

        // then — emails enviados al destinatario correcto
        verify(emailService).enviarResultadoAsignado(
                eq("estudiante1@eduplazas.es"), any(), any(), any(), any(), anyDouble());
        verify(emailService).enviarResultadoRechazado(
                eq("estudiante2@eduplazas.es"), any(), any());
    }

    // ── Caso 3 ───────────────────────────────────────────────────────────────

    /**
     * La notaFinal almacenada en la Asignacion debe ser la nota ponderada real:
     *   notaBase(8.42) + Matemáticas II(6.21)×0.2 + Física(5.65)×0.2 = 10.792
     */
    @Test
    void procesarAsignaciones_conCriteriosDeAdmision_calculaNotaFinalCorrecta() {
        Convocatoria conv = convocatoria();
        Oferta       o8   = oferta8(upm(), 1);
        Estudiante   estA = estudianteA();

        Solicitud solA = solicitudEntregada(estA, conv, o8);

        when(solicitudRepository.findByConvocatoriaId(1L)).thenReturn(List.of(solA));
        when(ofertaRepository.findByConvocatoriaId(1L)).thenReturn(List.of(o8));

        // when
        asignacionService.procesarAsignaciones(1L);

        // then
        ArgumentCaptor<Asignacion> captor = ArgumentCaptor.forClass(Asignacion.class);
        verify(asignacionRepository).save(captor.capture());

        // 8.42 + 6.21×0.2 + 5.65×0.2 = 10.792
        assertThat(captor.getValue().getNotaFinal()).isCloseTo(10.792, within(0.001));
    }

    // ── Caso 4 ───────────────────────────────────────────────────────────────

    /**
     * El algoritmo respeta el orden de preferencia: aunque el estudiante podría
     * acceder a la oferta 6 (120 plazas), debe ser asignado a la oferta 8
     * (1ª preferencia, 1 plaza disponible).
     *
     *   Preferencia 1 → oferta 8  (notaPonderada=10.792)
     *   Preferencia 2 → oferta 6  (notaPonderada=10.227, nunca evaluada)
     */
    @Test
    void procesarAsignaciones_estudianteConPlazaEnPrimeraPreferencia_noEvaluaSegundaPreferencia() {
        Convocatoria conv = convocatoria();
        Universidad  uni  = upm();
        Oferta       o8   = oferta8(uni, 1);    // 1ª pref, 1 plaza
        Oferta       o6   = oferta6(uni);        // 2ª pref, 120 plazas

        Estudiante estA = estudianteA();
        Solicitud  solA = solicitudEntregada(estA, conv, o8, o6);

        when(solicitudRepository.findByConvocatoriaId(1L)).thenReturn(List.of(solA));
        when(ofertaRepository.findByConvocatoriaId(1L)).thenReturn(List.of(o8, o6));

        // when
        asignacionService.procesarAsignaciones(1L);

        // then — Asignacion creada con la primera preferencia (oferta 8)
        ArgumentCaptor<Asignacion> captor = ArgumentCaptor.forClass(Asignacion.class);
        verify(asignacionRepository).save(captor.capture());

        Asignacion asig = captor.getValue();
        assertThat(asig.getOferta().getId()).isEqualTo(8L);
        assertThat(asig.getNotaFinal()).isCloseTo(10.792, within(0.001));
    }
}
