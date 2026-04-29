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

    public Convocatoria guardar(Convocatoria convocatoria) {
        return convocatoriaRepository.save(convocatoria);
    }

    // Comprueba cada minuto si alguna convocatoria abierta ha superado su fecha de cierre
    @Scheduled(fixedRate = 60000)
    public void cerrarConvocatoriasVencidas() {
        Optional<Convocatoria> convocatoriaOpt =
                convocatoriaRepository.findByEstado(EstadoConvocatoriaEnum.ABIERTA);

        if (convocatoriaOpt.isEmpty()) return;

        Convocatoria convocatoria = convocatoriaOpt.get();
        if (!LocalDate.now().isAfter(convocatoria.getFechaCierreConvocatoria())) return;

        convocatoria.setEstado(EstadoConvocatoriaEnum.CERRADA);
        convocatoriaRepository.save(convocatoria);

        asignacionService.procesarAsignaciones(convocatoria.getId());
    }
}