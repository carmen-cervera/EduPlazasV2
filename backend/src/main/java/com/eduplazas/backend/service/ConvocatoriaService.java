package com.eduplazas.backend.service;

import com.eduplazas.backend.model.Convocatoria;
import com.eduplazas.backend.model.EstadoConvocatoriaEnum;
import com.eduplazas.backend.repository.ConvocatoriaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ConvocatoriaService {

    private final ConvocatoriaRepository convocatoriaRepository;
    private final AsignacionService asignacionService;

    public ConvocatoriaService(ConvocatoriaRepository convocatoriaRepository,
                               AsignacionService asignacionService) {
        this.convocatoriaRepository = convocatoriaRepository;
        this.asignacionService = asignacionService;
    }

    public List<Convocatoria> obtenerTodas() {
        return convocatoriaRepository.findAll();
    }

    public Optional<Convocatoria> obtenerAbierta() {
        return convocatoriaRepository.findByEstado(EstadoConvocatoriaEnum.ABIERTA);
    }

    public Convocatoria crearConvocatoria(String cursoAcademico,
                                          LocalDate fechaApertura,
                                          LocalDate fechaCierre) {
        if (!fechaCierre.isAfter(fechaApertura)) {
            throw new RuntimeException("La fecha de cierre debe ser posterior a la fecha de apertura");
        }

        if (convocatoriaRepository.existsByFechaCierreConvocatoriaAfter(fechaApertura)) {
            throw new RuntimeException(
                "La fecha de apertura de la nueva convocatoria debe ser posterior " +
                "a la fecha de cierre de la convocatoria anterior"
            );
        }

        Convocatoria convocatoria = new Convocatoria();
        convocatoria.setCursoAcademico(cursoAcademico);
        convocatoria.setFechaApertura(fechaApertura);
        convocatoria.setFechaCierreConvocatoria(fechaCierre);

        if (!LocalDate.now().isBefore(fechaApertura)) {
            convocatoria.setEstado(EstadoConvocatoriaEnum.ABIERTA);
        } else {
            convocatoria.setEstado(EstadoConvocatoriaEnum.PENDIENTE);
        }

        return convocatoriaRepository.save(convocatoria);
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 10000)
    public void actualizarEstadoConvocatorias() {
        LocalDate hoy = LocalDate.now();

        List<Convocatoria> todas = convocatoriaRepository.findAll();
        for (Convocatoria conv : todas) {
            if (conv.getEstado() == EstadoConvocatoriaEnum.PENDIENTE
                    && !hoy.isBefore(conv.getFechaApertura())) {
                conv.setEstado(EstadoConvocatoriaEnum.ABIERTA);
                convocatoriaRepository.save(conv);
            }
        }

        Optional<Convocatoria> abiertaOpt =
                convocatoriaRepository.findByEstado(EstadoConvocatoriaEnum.ABIERTA);

        if (abiertaOpt.isEmpty()) return;

        Convocatoria abierta = abiertaOpt.get();
        if (!hoy.isAfter(abierta.getFechaCierreConvocatoria())) return;

        abierta.setEstado(EstadoConvocatoriaEnum.CERRADA);
        convocatoriaRepository.save(abierta);

        asignacionService.procesarAsignaciones(abierta.getId());
    }
}