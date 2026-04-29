package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Solicitud;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface SolicitudRepository extends CrudRepository<Solicitud, Long> {
    Optional<Solicitud> findByEstudianteId(Long estudianteId);
    Optional<Solicitud> findByEstudianteIdAndConvocatoriaId(Long estudianteId, Long convocatoriaId);
    List<Solicitud> findByConvocatoriaId(Long convocatoriaId);
    List<Solicitud> findAll();
}