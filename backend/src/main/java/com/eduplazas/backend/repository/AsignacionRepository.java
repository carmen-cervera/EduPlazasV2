package com.eduplazas.backend.repository;

import com.eduplazas.backend.model.Asignacion;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface AsignacionRepository extends CrudRepository<Asignacion, Long> {
    Optional<Asignacion> findBySolicitudId(Long solicitudId);
    List<Asignacion> findByOfertaId(Long ofertaId);
    List<Asignacion> findAll();
}